package org.jsmart.zerocode.core.runner.parallel;

import org.jsmart.zerocode.parallel.ExecutorServiceRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDateTime.now;

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

    public boolean process() {
        executorServiceRunner.runRunnables();

        LOGGER.info(
                  "\n------------------------------------"
                + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                + "\n   >> Passed count:" + passedCounter.get()
                + "\n   >> Failed count:" + failedCounter.get()
                + "\n------------------------------------");

        if(failedCounter.get() > 0){
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

    public boolean processMultiLoad() {
        executorServiceRunner.runRunnablesMulti();

        LOGGER.info(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        if(failedCounter.get() > 0){
            return failed;
        }

        return passed;
    }
}
