package org.jsmart.zerocode.jupiter.load;

import org.jsmart.zerocode.core.runner.parallel.LoadProcessor;
import org.jsmart.zerocode.jupiter.listener.ZeroCodeTestReportJupiterListener;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.LocalDateTime.now;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class JupiterLoadProcessor extends LoadProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JupiterLoadProcessor.class);

    public JupiterLoadProcessor(String loadPropertiesFile) {
        super(loadPropertiesFile);
    }

    public LoadProcessor addJupiterTest(Class<?> testClass, String testMethod) {

        Runnable zeroCodeJunitTest = createJupiterRunnable(testClass, testMethod);

        getExecutorServiceRunner().addRunnable(zeroCodeJunitTest);

        return this;
    }

    public void updatePassFailCount(SummaryGeneratingListener summaryListener) {
        TestExecutionSummary summary = summaryListener.getSummary();
        if (summary.getTotalFailureCount() > 0) {
            getFailedCounter().incrementAndGet();
            summary.getFailures().forEach(thisFailure -> {
                TestIdentifier testIdentifier = thisFailure.getTestIdentifier();
                String exceptionMessage = thisFailure.getException().getMessage();
                LOGGER.info("\n----------------------------------------------------------------------\n");
                LOGGER.info("\n###JUnit5: Test Failed Due To --> {}, \ntestIdentifier={}", exceptionMessage, testIdentifier);
                LOGGER.info("\n----------------------------------------------------------------------\n");
            });
        } else {
            getPassedCounter().incrementAndGet();
        }
    }

    private void registerReportListener(Class<?> testClass, String testMethod, Launcher launcher) {
        ZeroCodeTestReportJupiterListener reportListener =
                new ZeroCodeTestReportJupiterListener(testClass, testMethod );
        launcher.registerTestExecutionListeners(reportListener);
    }

    private Runnable createJupiterRunnable(Class<?> testClass, String testMethod) {
        return () -> {

            LOGGER.debug(Thread.currentThread().getName() + "\n - Parallel Junit5 test- *Start-Time = " + now());

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
            LOGGER.debug(Thread.currentThread().getName() + "\n   - Parallel Junit5 test- *End-Time = " + now());

            updatePassFailCount(summaryListener);

        };
    }
}
