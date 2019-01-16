package org.jsmart.zerocode.parallel.simple;

import org.jsmart.zerocode.parallel.ExecutorServiceRunner;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LoadTest {

    @Test
    public void testLoad_Pass() {
        ExecutorServiceRunner executorServiceRunner = new ExecutorServiceRunner("load_config_test.properties");

        final AtomicInteger passedCounter = new AtomicInteger();
        final AtomicInteger failedCounter = new AtomicInteger();

        Runnable taskSampleTest = () -> {
            System.out.println(Thread.currentThread().getName() + " JunitTestSample test- Start. Time = " + LocalDateTime.now());

            Result result = (new JUnitCore()).run(Request.method(JunitTestSample.class, "testFirstName"));

            System.out.println(Thread.currentThread().getName() + " JunitTestSample test- *Finished Time, result = " + LocalDateTime.now() + " -" + result.wasSuccessful());

            if(result.wasSuccessful()){
                passedCounter.incrementAndGet();
            } else {
                failedCounter.incrementAndGet();
            }
        };

        executorServiceRunner.addRunnable(taskSampleTest);
        executorServiceRunner.runRunnables();

        System.out.println(">>> passed count:" + passedCounter.get());
        System.out.println(">>> failed count:" + failedCounter.get());
        System.out.println(">>> Total test count:" + (failedCounter.get() + passedCounter.get()));

        assertThat(failedCounter.get(), is(0));
    }

    @Test
    public void testLoad_Fail() {
        ExecutorServiceRunner executorServiceRunner = new ExecutorServiceRunner("load_config_test.properties");

        final AtomicInteger passedCounter = new AtomicInteger();
        final AtomicInteger failedCounter = new AtomicInteger();

        Runnable taskSampleTest = () -> {
            System.out.println(Thread.currentThread().getName() + " JunitTestSample test- Start. Time = " + LocalDateTime.now());

            Result result = (new JUnitCore()).run(Request.method(JunitTestSample.class, "testFirstName_fail"));

            System.out.println(Thread.currentThread().getName() + " JunitTestSample test- *Finished Time, result = " + LocalDateTime.now() + " -" + result.wasSuccessful());

            if(result.wasSuccessful()){
                passedCounter.incrementAndGet();
            } else {
                failedCounter.incrementAndGet();
            }
        };

        executorServiceRunner.addRunnable(taskSampleTest);
        executorServiceRunner.runRunnables();

        System.out.println(">>> passed count:" + passedCounter.get());
        System.out.println(">>> failed count:" + failedCounter.get());
        System.out.println(">>> Total test count:" + (failedCounter.get() + passedCounter.get()));

        assertThat(passedCounter.get(), is(0));
    }

}

