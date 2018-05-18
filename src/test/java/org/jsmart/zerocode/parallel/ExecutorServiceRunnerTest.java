package org.jsmart.zerocode.parallel;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ExecutorServiceRunnerTest {

    @Test
    public void testLongDivision() {
        int rampUpPeriod = 1;
        int numberOfThreads = 5;

        Double delayBetweenTwoThreadsInMilliSecs = (Double.valueOf(rampUpPeriod) / Double.valueOf(numberOfThreads)) * 1000L;

        System.out.println("delay2: " + (1D / 5D));

        assertThat((1D / 5D) * 1000, is(200.0D));
        assertThat(delayBetweenTwoThreadsInMilliSecs, is(200.0));
    }

    @Test
    public void testLocalDateTime_now() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("now: " + LocalDateTime.now());
            System.out.println("UUID: " + UUID.randomUUID());
        }

        for (int i = 0; i < 10; i++) {
            Thread.sleep(50L);
            System.out.println("now: " + LocalDateTime.now());
            System.out.println("UUID: " + UUID.randomUUID());
        }
    }
}