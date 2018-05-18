package org.jsmart.zerocode.parallel.simple;

import org.jsmart.zerocode.parallel.ExecutorServiceRunner;
import org.jsmart.zerocode.parallel.TestGitHubApi;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadTestMain {

    public static void main(String[] args) {
        ExecutorServiceRunner executorServiceRunner = new ExecutorServiceRunner("load_config_test.properties");
        //ExecutorServiceRunner executorServiceRunner = new ExecutorServiceRunner(5, 1, 10);

        Runnable taskGitHubApiTest = () -> {
            System.out.println(Thread.currentThread().getName() + " JUnit test- Start. Time = " + LocalDateTime.now());

            Result result = (new JUnitCore()).run(Request.method(TestGitHubApi.class, "testGitHubApi_get"));

            System.out.println(Thread.currentThread().getName() + " JUnit test- *Finished Time, result = " + LocalDateTime.now() + " -" + result.wasSuccessful());

        };

        Runnable taskSampleTest = () -> {
            System.out.println(Thread.currentThread().getName() + " JunitTestSample test- Start. Time = " + LocalDateTime.now());

            Result result = (new JUnitCore()).run(Request.method(JunitTestSample.class, "testFirstName"));

            System.out.println(Thread.currentThread().getName() + " JunitTestSample test- *Finished Time, result = " + LocalDateTime.now() + " -" + result.wasSuccessful());

        };

        executorServiceRunner.addRunnable(taskSampleTest);

        executorServiceRunner.runRunnables();
    }

    /**
     * case 1:
     * ExecutorService executor = Executors.newFixedThreadPool(1);
     * long timeTakenByTaskToCompleteExecution = 1000L;
     * int numberOfRequestsDuringRampUp = 5; // simply does not make sense. Keep it same as threadPool size-
     * (otherwise if this is more than threadPool size, e.g. 10, then there are no threads left after 5 threads are
     * used up)
     * long delayBetweenRequestFiring = 2000L;
     * Explain:
     * Simply this will not fire requests in every 2 secs, as there are no threads left in the pool until 30secs when
     * thread1 is available to be picked up.
     * So if you make threadPool=2, then two requests are fired in gap of 2 secs.
     * e.g.
     * 9:30:00 - thread 1 - start
     * 9:30:02 - thread 2 - start
     * 9:30:30 - thread 1 - *end
     * 9:30:32 - thread 2 - *end
     * <p>
     * Hence if you want the requests to be fired every 2 secs, then you need threadPool=15, so that after
     * 9:30:28 - thread 15 - start (may not be n the same order, but one of 15 threads will be picked)
     * 9:30:30 - thread 1 - *end
     * 9:30:30 - thread 1 - start <--- started again as it was available in the pool after completing the task.
     * 9:30:32 - thread 2 - *end
     * 9:30:32 - thread 2 - start
     */
    public static void mainSS(String[] args) {
        int threadPoolSize = 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        long timeTakenByTaskToCompleteExecution = 30000L;
        int numberOfRequestsDuringRampUp = 10; // simply does not make sense. Keep it same as threadPool size.
        long rampUpTime = 10000L;
        long delayBetweenRequestFiring = (rampUpTime / numberOfRequestsDuringRampUp > threadPoolSize ?
                threadPoolSize :
                numberOfRequestsDuringRampUp) * 1000L; // this means, if it is less, then ok to proceed, as there will ne enough threads in the pool to execute in timely manner.
        int loop = 2;

        Runnable task1 = () -> {
            System.out.println(Thread.currentThread().getName() + " 1st- Start. Time = " + LocalDateTime.now());
            try {
                Thread.sleep(timeTakenByTaskToCompleteExecution);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " 1st- Finished* Time = " + LocalDateTime.now());

        };

        Runnable task2 = () -> {
            System.out.println(Thread.currentThread().getName() + " 2nd- Start. Time = " + LocalDateTime.now());
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " 2nd- Finished* Time = " + LocalDateTime.now());

        };

        Runnable taskUnitTest = () -> {
            System.out.println(Thread.currentThread().getName() + " JUnit test- Start. Time = " + LocalDateTime.now());

            Result result = (new JUnitCore()).run(Request.method(JunitTestSample.class, "testFirstName"));

            System.out.println(Thread.currentThread().getName() + " JUnit test- *Finished Time = " + LocalDateTime.now() + " -" + result.wasSuccessful());
        };


        int k = 0;
        for (int j = 0; j < loop; j++) {
            for (int i = 0; i < numberOfRequestsDuringRampUp; i++) {
                try {
                    System.out.println("Loop count- " + k++);
                    Thread.sleep(delayBetweenRequestFiring);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //executor.execute(taskUnitTest);
                executor.execute(task1);
                //executor.execute(task2);
                //executor.execute(() -> (new MockHttpExecutor()).invoke("task 3"));
            }
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
            //wait for all tasks to finish
            //System.out.println("Still waiting for all threads to complete execution...");
        }
        System.out.println("Now Finished all threads execution");
    }

}

