package org.jsmart.zerocode.core.runner.parallel;

import java.util.concurrent.atomic.AtomicInteger;
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestReportJupiterListener;
import org.jsmart.zerocode.parallel.ExecutorServiceRunner;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.LocalDateTime.now;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class LoadProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadProcessor.class);

    private final String loadPropertiesFile;
    private final AtomicInteger passedCounter = new AtomicInteger();
    private final AtomicInteger failedCounter = new AtomicInteger();
    private ExecutorServiceRunner executorServiceRunner;
    private boolean failed = true;
    private boolean passed = !failed;

    public LoadProcessor(String loadPropertiesFile) {
        this.loadPropertiesFile = loadPropertiesFile;
        executorServiceRunner = new ExecutorServiceRunner(loadPropertiesFile);
    }

    public LoadProcessor addTest(Class<?> testClass, String testMethod) {

        Runnable zeroCodeJunitTest = createRunnable(testClass, testMethod);

        executorServiceRunner.addRunnable(zeroCodeJunitTest);

        return this;
    }

    public LoadProcessor addJupiterTest(Class<?> testClass, String testMethod) {

        Runnable zeroCodeJunitTest = createJupiterRunnable(testClass, testMethod);

        executorServiceRunner.addRunnable(zeroCodeJunitTest);

        return this;
    }

    public boolean process() {
        executorServiceRunner.runRunnables();

        LOGGER.info(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        if (failedCounter.get() > 0) {
            return failed;
        }

        return passed;
    }

    public boolean processMultiLoad() {
        executorServiceRunner.runRunnablesMulti();

        LOGGER.info(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        if (failedCounter.get() > 0) {
            return failed;
        }

        return passed;
    }

    private Runnable createJupiterRunnable(Class<?> testClass, String testMethod) {
        return () -> {

            LOGGER.info(Thread.currentThread().getName() + "\n - Parallel Junit5 test- *Start-Time = " + now());

            final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectMethod(testClass, testMethod))
                    .build();
            final Launcher launcher = LauncherFactory.create();

            // -------------------------
            // Register Report listener
            // -------------------------
            registerReportListener(testClass, testMethod, launcher);

            // ------------------------------
            // Register Test-Summary listener
            // ------------------------------
            final SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(summaryListener);

            launcher.execute(request);
            LOGGER.info(Thread.currentThread().getName() + "\n   - Parallel Junit5 test- *End-Time = " + now());

            updatePassFailCount(summaryListener);

        };
    }

    private void updatePassFailCount(SummaryGeneratingListener summaryListener) {
        TestExecutionSummary summary = summaryListener.getSummary();
        if (summary.getTotalFailureCount() > 0) {
            failedCounter.incrementAndGet();
            summary.getFailures().forEach(thisFailure -> {
                TestIdentifier testIdentifier = thisFailure.getTestIdentifier();
                String exceptionMessage = thisFailure.getException().getMessage();
                LOGGER.info("\n----------------------------------------------------------------------\n");
                LOGGER.info("\n###JUnit5: Test Failed Due To --> {}, \ntestIdentifier={}", exceptionMessage, testIdentifier);
                LOGGER.info("\n----------------------------------------------------------------------\n");
            });
        } else {
            passedCounter.incrementAndGet();
        }
    }

    private void registerReportListener(Class<?> testClass, String testMethod, Launcher launcher) {
        ZeroCodeTestReportJupiterListener reportListener =
                new ZeroCodeTestReportJupiterListener(testClass, testMethod );
        launcher.registerTestExecutionListeners(reportListener);
    }

    private Runnable createRunnable(Class<?> testClass, String testMathod) {
        return () -> {
            LOGGER.info(Thread.currentThread().getName() + " Parallel Junit test- *Start. Time = " + now());

            Result result = (new JUnitCore()).run(Request.method(testClass, testMathod));

            LOGGER.info(Thread.currentThread().getName() + " Parallel Junit test- *  End. Time = " + now());

            if (result.wasSuccessful()) {
                passedCounter.incrementAndGet();
            } else {
                failedCounter.incrementAndGet();
            }
        };
    }

}
