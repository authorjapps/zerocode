package org.jsmart.zerocode.parallel;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ExecutorServiceRunnerTimeoutTest {

    @Test(expected = RuntimeException.class)
    public void testTimeoutFunctionality() {
        // Create an ExecutorServiceRunner with 1 thread, 10 loops, 1 second ramp up
        // and a timeout of 1 second
        ExecutorServiceRunner runner = new ExecutorServiceRunner(1, 10, 1, 1); // 1 second timeout
        
        // Add a runnable that will take some time to execute (more than timeout)
        runner.addRunnable(() -> {
            try {
                // Sleep for 3 seconds which is more than the 1 second timeout
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // This should throw a RuntimeException due to timeout
        runner.runRunnables();
    }
    
    @Test
    public void testTimeoutNotExceeded() {
        // Create an ExecutorServiceRunner with 1 thread, 1 loop, 1 second ramp up
        // and a timeout of 5 seconds
        ExecutorServiceRunner runner = new ExecutorServiceRunner(1, 1, 1, 5); // 5 second timeout
        
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        
        // Add a runnable that completes before timeout
        runner.addRunnable(() -> {
            taskExecuted.set(true);
            try {
                // Sleep for 1 second which is less than the 5 second timeout
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // This should complete without throwing an exception
        runner.runRunnables();
        
        assertTrue("Task should have been executed", taskExecuted.get());
    }
    
    @Test
    public void testTimeoutWithMultipleRunnables() {
        // Create an ExecutorServiceRunner with 2 threads, 2 loops, 1 second ramp up
        // and a timeout of 1 second
        ExecutorServiceRunner runner = new ExecutorServiceRunner(2, 2, 1, 1); // 1 second timeout
        
        // Add runnables that will take more time than the timeout
        for (int i = 0; i < 4; i++) {
            runner.addRunnable(() -> {
                try {
                    // Sleep for 2 seconds which is more than the 1 second timeout
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // This should throw a RuntimeException due to timeout
        try {
            runner.runRunnables();
            fail("Expected RuntimeException due to timeout");
        } catch (RuntimeException e) {
            assertTrue("Exception message should mention timeout", e.getMessage().contains("abort.after.time.lapsed.in.seconds"));
        }
    }
    
    @Test
    public void testTimeoutDisabledWhenNegative() {
        // Create an ExecutorServiceRunner with negative timeout value (disabled)
        ExecutorServiceRunner runner = new ExecutorServiceRunner(1, 1, 1, -1); // No timeout
        
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        
        // Add a runnable that executes
        runner.addRunnable(() -> {
            taskExecuted.set(true);
            try {
                Thread.sleep(500); // Sleep for 0.5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // This should complete without throwing an exception
        runner.runRunnables();
        
        assertTrue("Task should have been executed", taskExecuted.get());
    }
}