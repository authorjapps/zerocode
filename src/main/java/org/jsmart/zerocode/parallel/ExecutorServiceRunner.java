package org.jsmart.zerocode.parallel;


import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class ExecutorServiceRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeUnitRunner.class);

    private List<Runnable> runnables = new ArrayList<>();

    private int numberOfThreads;
    private int rampUpPeriod;
    private int loopCount;

    private Double delayBetweenTwoThreadsInMilliSecs;

    public ExecutorServiceRunner() {
        delayBetweenTwoThreadsInMilliSecs = (Double.valueOf(rampUpPeriod) / Double.valueOf(numberOfThreads)) * 1000L;
    }

    public ExecutorServiceRunner(String loadPropertiesFile) {
        Properties properties = PropertiesProvider.getProperties(loadPropertiesFile);
        numberOfThreads = parseInt(properties.getProperty("number.of.threads"));
        rampUpPeriod = parseInt(properties.getProperty("ramp.up.period"));
        loopCount = parseInt(properties.getProperty("loop.count"));

        calculateAndSetDelayBetweenTwoThreadsInSecs(rampUpPeriod);

        logLoadingProperties();
    }

    public ExecutorServiceRunner(int numberOfThreads, int loopCount, int rampUpPeriod) {
        this.numberOfThreads = numberOfThreads;
        this.loopCount = loopCount;
        this.rampUpPeriod = rampUpPeriod;

        calculateAndSetDelayBetweenTwoThreadsInSecs(rampUpPeriod);
        logLoadingProperties();
    }

    public ExecutorServiceRunner addRunnable(Runnable runnable) {
        runnables.add(runnable);
        return this;
    }


    public void runRunnables() {
        if (runnables == null || runnables.size() == 0) {
            throw new RuntimeException("No runnable were found to run. You can add one or more runnables using 'addRunnable(Runnable runnable)'");
        }
        runRunnables(runnables);
    }

    public void runRunnables(List<Runnable> runnables) {

        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            for (int i = 0; i < loopCount; i++) {
                runnables.stream().forEach(thisFunction -> {
                    for (int j = 0; j < numberOfThreads; j++) {
                        LOGGER.info(Thread.currentThread().getName() + " JUnit test- Start. Time = " + LocalDateTime.now());
                        try {
                            LOGGER.info("Waiting in the transit for next flight to adjust overall ramp up time, wait time now = " + delayBetweenTwoThreadsInMilliSecs);
                            Thread.sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        executorService.execute(thisFunction);

                        LOGGER.info(Thread.currentThread().getName() + " JUnit test- *Finished Time = " + LocalDateTime.now());
                    }
                });
            }
        } catch (Exception interruptEx) {
            throw new RuntimeException(interruptEx);
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                //wait for all tasks to finish execution
                //LOGGER.info("Still waiting for all threads to complete execution...");
            }
            LOGGER.info("**Finished executing all threads**");
        }
    }


    public void runCallables(List<Callable<Object>> callables) {

        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            executorService.invokeAll(callables).stream().forEach(future -> {
                for (int j = 0; j < numberOfThreads; j++) {
                    LOGGER.info(Thread.currentThread().getName() + " Future execution- Start. Time = " + LocalDateTime.now());
                    try {
                        Thread.sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    execute(future);

                    LOGGER.info(Thread.currentThread().getName() + " Future execution- *Finished Time = " + LocalDateTime.now());
                }
            });
        } catch (InterruptedException interruptEx) {
            throw new RuntimeException(interruptEx);
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // wait for all tasks to finish executing
                // LOGGER.info("Still waiting for all threads to complete execution...");
            }
            LOGGER.info("Finished all threads");
        }


    }

    public <T extends Object> Callable<Object> createCallableFuture(T objectToConsumer, Consumer<T> consumer) {
        return () -> {
            consumer.accept(objectToConsumer);
            return true;
        };
    }

    private Object execute(Future<Object> future) {
        try {
            LOGGER.info("executing Future now...");
            return future.get();
        } catch (Exception futureEx) {
            if (futureEx.getCause() instanceof InternalServerErrorException) {
                throw (InternalServerErrorException) futureEx.getCause();
            }
            throw new RuntimeException(futureEx);
        }
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public int getRampUpPeriod() {
        return rampUpPeriod;
    }


    private void calculateAndSetDelayBetweenTwoThreadsInSecs(int rampUpPeriod) {
        if (rampUpPeriod == 0) {
            delayBetweenTwoThreadsInMilliSecs = 0D;

        } else {

            delayBetweenTwoThreadsInMilliSecs = (Double.valueOf(rampUpPeriod) / Double.valueOf(numberOfThreads)) * 1000L;
        }
    }

    public List<Runnable> getRunnables() {
        return runnables;
    }


    private void logLoadingProperties() {
        LOGGER.info("### numberOfThreads : " + numberOfThreads);
        LOGGER.info("### rampUpPeriod : " + rampUpPeriod);
        LOGGER.info("### loopCount : " + loopCount);
    }
}
