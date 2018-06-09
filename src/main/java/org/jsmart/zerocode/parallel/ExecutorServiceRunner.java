package org.jsmart.zerocode.parallel;


import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jsmart.zerocode.core.utils.PropertiesProviderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static java.lang.Double.valueOf;
import static java.lang.Integer.parseInt;
import static java.lang.Thread.sleep;
import static java.time.LocalDateTime.now;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class ExecutorServiceRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceRunner.class);

    private final List<Runnable> runnables = new ArrayList<>();
    private final List<Callable<Object>> callables = new ArrayList<>();

    private int numberOfThreads;
    private int rampUpPeriod;
    private int loopCount;

    private Double delayBetweenTwoThreadsInMilliSecs;

    public ExecutorServiceRunner(String loadPropertiesFile) {
        Properties properties = PropertiesProviderUtils.getProperties(loadPropertiesFile);
        numberOfThreads = parseInt(properties.getProperty("number.of.threads"));
        rampUpPeriod = parseInt(properties.getProperty("ramp.up.period.in.seconds"));
        loopCount = parseInt(properties.getProperty("loop.count"));

        calculateAndSetDelayBetweenTwoThreadsInSecs(rampUpPeriod);

        logLoadingProperties();
    }

    public ExecutorServiceRunner(int numberOfThreads, int loopCount, int rampUpPeriod) {
        this.numberOfThreads = numberOfThreads;
        this.loopCount = loopCount;
        this.rampUpPeriod = rampUpPeriod;

        calculateAndSetDelayBetweenTwoThreadsInSecs(this.rampUpPeriod);
        logLoadingProperties();
    }

    public ExecutorServiceRunner addRunnable(Runnable runnable) {
        runnables.add(runnable);
        return this;
    }

    public ExecutorServiceRunner addCallable(Callable callable) {
        callables.add(callable);
        return this;
    }


    public void runRunnables() {

        if (runnables == null || runnables.size() == 0) {
            throw new RuntimeException("No runnable(s) was found to run. You can add one or more runnables using 'addRunnable(Runnable runnable)'");
        }

        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            for (int i = 0; i < loopCount; i++) {
                runnables.stream().forEach(thisFunction -> {
                    for (int j = 0; j < numberOfThreads; j++) {
                        try {
                            LOGGER.info("Waiting for the next test flight to adjust the overall ramp up time, " +
                                    "waiting time in the transit now = " + delayBetweenTwoThreadsInMilliSecs);
                            sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        LOGGER.info(Thread.currentThread().getName() + " Executor - *Start... Time = " + now());

                        executorService.execute(thisFunction);

                        LOGGER.info(Thread.currentThread().getName() + " Executor - *Finished Time = " + now());
                    }
                });
            }
        } catch (Exception interruptEx) {
            throw new RuntimeException(interruptEx);
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // --------------------------------------
                // wait for all tasks to finish execution
                // --------------------------------------
                //LOGGER.info("Still waiting for all threads to complete execution...");
            }
            LOGGER.info("**Finished executing all threads**");
        }
    }

    public void runRunnablesMulti() {
        if (runnables == null || runnables.size() == 0) {
            throw new RuntimeException("No runnable(s) was found to run. You can add one or more runnables using 'addRunnable(Runnable runnable)'");
        }

        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            final AtomicInteger functionIndex = new AtomicInteger();

            for (int i = 0; i < loopCount; i++) {
                for (int j = 0; j < numberOfThreads; j++) {
                    try {
                        LOGGER.info("Waiting for the next test flight to adjust the overall ramp up time, " +
                                "waiting time in the transit now = " + delayBetweenTwoThreadsInMilliSecs);
                        sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    LOGGER.info(Thread.currentThread().getName() + " Executor - *Start... Time = " + now());

                    executorService.execute(runnables.get(functionIndex.getAndIncrement()));

                    LOGGER.info(Thread.currentThread().getName() + " Executor - *Finished Time = " + now());

                    if(functionIndex.get() == runnables.size()){
                        functionIndex.set(0);
                    }
                }

            }
        } catch (Exception interruptEx) {
            throw new RuntimeException(interruptEx);
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // --------------------------------------
                // wait for all tasks to finish execution
                // --------------------------------------
                //LOGGER.info("Still waiting for all threads to complete execution...");
            }
            LOGGER.info("**Finished executing all threads**");
        }
    }

    public void runCallables() {
        runCallableFutures();
    }

    public void runCallableFutures() {

        if (callables == null || callables.size() == 0) {
            throw new RuntimeException("No callable(s) was found to run. You can add one or more callables using 'addCallable(Callable callable)'");
        }

        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            executorService.invokeAll(callables).stream().forEach(future -> {
                for (int j = 0; j < numberOfThreads; j++) {
                    try {
                        LOGGER.info("Waiting in the transit for next test flight to adjust overall ramp up time, wait time now = " + delayBetweenTwoThreadsInMilliSecs);
                        sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    LOGGER.info(Thread.currentThread().getName() + " Future execution- Start.... Time = " + now());

                    execute(future);

                    LOGGER.info(Thread.currentThread().getName() + " Future execution- *Finished Time = " + now());
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
            LOGGER.info("executing the 'Future' now...");
            return future.get();
        } catch (Exception futureEx) {
            if (futureEx.getCause() instanceof InternalServerErrorException) {
                throw (InternalServerErrorException) futureEx.getCause();
            }
            throw new RuntimeException(futureEx);
        }
    }


    private void calculateAndSetDelayBetweenTwoThreadsInSecs(int rampUpPeriod) {
        if (rampUpPeriod == 0) {
            delayBetweenTwoThreadsInMilliSecs = 0D;
        } else {
            delayBetweenTwoThreadsInMilliSecs = (valueOf(rampUpPeriod) / valueOf(numberOfThreads)) * 1000L;
        }
    }

    public List<Runnable> getRunnables() {
        return runnables;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public int getRampUpPeriod() {
        return rampUpPeriod;
    }

    public List<Callable<Object>> getCallables() {
        return callables;
    }

    private void logLoadingProperties() {
        LOGGER.info(
                "\nLOAD:" +
                "\n-----------------------------------" +
                "\n   ### numberOfThreads : " + numberOfThreads +
                "\n   ### rampUpPeriodInSeconds : " + rampUpPeriod +
                "\n   ### loopCount : " + loopCount +
                "\n-----------------------------------\n");

    }
}
