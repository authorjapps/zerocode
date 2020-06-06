package org.jsmart.zerocode.core.runner.parallel;

import java.util.concurrent.atomic.AtomicInteger;
import org.jsmart.zerocode.parallel.ExecutorServiceRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.LocalDateTime.now;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TARGET_FULL_REPORT_CSV_FILE_NAME;

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
        this.executorServiceRunner = createExecutorServiceRunner();
    }

    public ExecutorServiceRunner getExecutorServiceRunner() {
        return executorServiceRunner;
    }

    public AtomicInteger getPassedCounter() {
        return passedCounter;
    }

    public AtomicInteger getFailedCounter() {
        return failedCounter;
    }

    public String getLoadPropertiesFile() {
        return loadPropertiesFile;
    }

    public ExecutorServiceRunner createExecutorServiceRunner() {
        return new ExecutorServiceRunner(getLoadPropertiesFile());
    }

    public LoadProcessor addTest(Class<?> testClass, String testMethod) {

        Runnable zeroCodeJunitTest = createRunnable(testClass, testMethod);

        executorServiceRunner.addRunnable(zeroCodeJunitTest);

        return this;
    }

    public boolean process() {
        executorServiceRunner.runRunnables();

        LOGGER.debug(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        LOGGER.warn("=====>> Completed this load-run! <<=====\n" +
                "\n----------------------------------------\n"+
                "\n>>>> Number of load tests ran : " + (failedCounter.get() + passedCounter.get()) +
                "\n----------------------------------------\n" +
                ">>>>>>> View the full performance results in the 'target/" + TARGET_FULL_REPORT_CSV_FILE_NAME + "' folder.\n"
        );

        if (failedCounter.get() > 0) {
            return failed;
        }

        return passed;
    }

    public boolean processMultiLoad() {
        executorServiceRunner.runRunnablesMulti();

        LOGGER.debug(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        LOGGER.warn("=====>> Completed this load-run! <<=====\n" +
                        "\n----------------------------------------\n"+
                        "\n>>>> Number of load tests ran : " + (failedCounter.get() + passedCounter.get()) +
                        "\n----------------------------------------\n" +
                ">>>>>>> View the performance results in the 'target/" + TARGET_FULL_REPORT_CSV_FILE_NAME + "' folder.\n"
        );

        if (failedCounter.get() > 0) {
            return failed;
        }

        return passed;
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
