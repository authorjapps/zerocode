package org.jsmart.zerocode.parallel;


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
import static java.lang.Long.parseLong;
import static java.lang.Thread.sleep;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.ofInstant;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class ExecutorServiceRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceRunner.class);

    private final List<Runnable> runnables = new ArrayList<>();
    private final List<Callable<Object>> callables = new ArrayList<>();

    private int numberOfThreads;
    private int rampUpPeriod;
    private int loopCount;
    private long abortAfterTimeLapsedInSeconds;

    private Double delayBetweenTwoThreadsInMilliSecs;

    public ExecutorServiceRunner(String loadPropertiesFile) {
        Properties properties = PropertiesProviderUtils.getProperties(loadPropertiesFile);
        numberOfThreads = parseInt(properties.getProperty("number.of.threads"));
        rampUpPeriod = parseInt(properties.getProperty("ramp.up.period.in.seconds"));
        loopCount = parseInt(properties.getProperty("loop.count"));
        abortAfterTimeLapsedInSeconds = parseLong(properties.getProperty("abort.after.time.lapsed.in.seconds", "-1"));

        calculateAndSetDelayBetweenTwoThreadsInSecs(rampUpPeriod);

        logLoadingProperties();
    }

    public ExecutorServiceRunner(int numberOfThreads, int loopCount, int rampUpPeriod) {
        this(numberOfThreads, loopCount, rampUpPeriod, -1);
    }

    public ExecutorServiceRunner(int numberOfThreads, int loopCount, int rampUpPeriod, long abortAfterTimeLapsedInSeconds) {
        this.numberOfThreads = numberOfThreads;
        this.loopCount = loopCount;
        this.rampUpPeriod = rampUpPeriod;
        this.abortAfterTimeLapsedInSeconds = abortAfterTimeLapsedInSeconds;

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

        long startTime = System.currentTimeMillis();
        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            for (int i = 0; i < loopCount; i++) {
                // Check timeout before each loop iteration
                if (isTimeoutExceeded(startTime)) {
                    throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                }

                runnables.stream().forEach(thisFunction -> {
                    for (int j = 0; j < numberOfThreads; j++) {
                        // Check timeout before executing each thread
                        if (isTimeoutExceeded(startTime)) {
                            throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                        }

                        try {
                            LOGGER.debug("Waiting for the next test flight to adjust the overall ramp up time, " +
                                    "waiting time in the transit now = " + delayBetweenTwoThreadsInMilliSecs);
                            sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        // Check timeout again after sleep
                        if (isTimeoutExceeded(startTime)) {
                            throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                        }

                        LOGGER.debug(Thread.currentThread().getName() + " Executor - *Start... Time = " + now());

                        executorService.execute(thisFunction);

                        LOGGER.debug(Thread.currentThread().getName() + " Executor - *Finished Time = " + now());
                    }
                });
            }
        } catch (Exception interruptEx) {
            throw new RuntimeException(interruptEx);
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Check timeout while waiting for tasks to finish
                if (abortAfterTimeLapsedInSeconds > 0) {
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    if (elapsedSeconds >= abortAfterTimeLapsedInSeconds) {
                        LOGGER.warn("Timeout reached while waiting for tasks to complete. Initiating shutdown...");
                        executorService.shutdownNow();
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }
                }
                // --------------------------------------
                // wait for all tasks to finish execution
                // --------------------------------------
                try {
                    Thread.sleep(100); // Small sleep to prevent busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            LOGGER.debug("**Finished executing all threads**");
        }
    }

    public void runRunnablesMulti() {
        if (runnables == null || runnables.size() == 0) {
            throw new RuntimeException("No runnable(s) was found to run. You can add one or more runnables using 'addRunnable(Runnable runnable)'");
        }

        long startTime = System.currentTimeMillis();
        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            final AtomicInteger functionIndex = new AtomicInteger();

            for (int i = 0; i < loopCount; i++) {
                // Check timeout before each loop iteration
                if (isTimeoutExceeded(startTime)) {
                    throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                }

                for (int j = 0; j < numberOfThreads; j++) {
                    // Check timeout before executing each thread
                    if (isTimeoutExceeded(startTime)) {
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }

                    try {
                        LOGGER.debug("Waiting for the next test flight to adjust the overall ramp up time, " +
                                "waiting time in the transit now = " + delayBetweenTwoThreadsInMilliSecs);
                        sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Check timeout again after sleep
                    if (isTimeoutExceeded(startTime)) {
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }

                    LOGGER.debug(Thread.currentThread().getName() + " Executor - *Start... Time = " + now());

                    executorService.execute(runnables.get(functionIndex.getAndIncrement()));

                    LOGGER.debug(Thread.currentThread().getName() + " Executor - *Finished Time = " + now());

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
                // Check timeout while waiting for tasks to finish
                if (abortAfterTimeLapsedInSeconds > 0) {
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    if (elapsedSeconds >= abortAfterTimeLapsedInSeconds) {
                        LOGGER.warn("Timeout reached while waiting for tasks to complete. Initiating shutdown...");
                        executorService.shutdownNow();
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }
                }
                // --------------------------------------
                // wait for all tasks to finish execution
                // --------------------------------------
                try {
                    Thread.sleep(100); // Small sleep to prevent busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            LOGGER.warn("** Completed executing all virtual-user scenarios! **");
        }
    }

    public void runCallables() {
        runCallableFutures();
    }

    public void runCallableFutures() {

        if (callables == null || callables.size() == 0) {
            throw new RuntimeException("No callable(s) was found to run. You can add one or more callables using 'addCallable(Callable callable)'");
        }

        long startTime = System.currentTimeMillis();
        ExecutorService executorService = newFixedThreadPool(numberOfThreads);

        try {
            executorService.invokeAll(callables).stream().forEach(future -> {
                for (int j = 0; j < numberOfThreads; j++) {
                    // Check timeout before each thread execution
                    if (isTimeoutExceeded(startTime)) {
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }

                    try {
                        LOGGER.debug("Waiting in the transit for next test flight to adjust overall ramp up time, wait time now = " + delayBetweenTwoThreadsInMilliSecs);
                        sleep(delayBetweenTwoThreadsInMilliSecs.longValue());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Check timeout again after sleep
                    if (isTimeoutExceeded(startTime)) {
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }

                    LOGGER.debug(Thread.currentThread().getName() + " Future execution- Start.... Time = " + now());

                    execute(future);

                    LOGGER.debug(Thread.currentThread().getName() + " Future execution- *Finished Time = " + now());
                }
            });
        } catch (InterruptedException interruptEx) {
            throw new RuntimeException(interruptEx);
        } finally {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Check timeout while waiting for tasks to finish
                if (abortAfterTimeLapsedInSeconds > 0) {
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    if (elapsedSeconds >= abortAfterTimeLapsedInSeconds) {
                        LOGGER.warn("Timeout reached while waiting for tasks to complete. Initiating shutdown...");
                        executorService.shutdownNow();
                        throw new RuntimeException("Load test aborted: abort.after.time.lapsed.in.seconds of " + abortAfterTimeLapsedInSeconds + " seconds exceeded");
                    }
                }
                // wait for all tasks to finish executing
                // LOGGER.info("Still waiting for all threads to complete execution...");
                try {
                    Thread.sleep(100); // Small sleep to prevent busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            LOGGER.warn("* Completed executing all virtual-user scenarios! *");
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
            LOGGER.debug("executing the 'Future' now...");
            return future.get();
        } catch (Exception futureEx) {
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
        LOGGER.warn(
                "\nLOAD:" +
                "\n-----------------------------------" +
                "\n   ### numberOfThreads : " + numberOfThreads +
                "\n   ### rampUpPeriodInSeconds : " + rampUpPeriod +
                "\n   ### loopCount : " + loopCount +
                "\n-----------------------------------\n");

    }
    
    private boolean isTimeoutExceeded(long startTime) {
        if (abortAfterTimeLapsedInSeconds <= 0) {
            return false; // No timeout configured
        }
        
        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        return elapsedSeconds >= abortAfterTimeLapsedInSeconds;
    }
}
