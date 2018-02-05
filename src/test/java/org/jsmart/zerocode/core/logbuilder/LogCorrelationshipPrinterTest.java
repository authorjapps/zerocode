package org.jsmart.zerocode.core.logbuilder;

import org.junit.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.logbuilder.LogCorrelationshipPrinter.durationMilliSecBetween;

public class LogCorrelationshipPrinterTest {

    @Test
    public void testResponseDelayInMilliSec() throws Exception {

        LocalDateTime startTime;
        LocalDateTime endTime;
        double delayInMilliSec;

        startTime = parse("2018-02-05T19:46:32.001");
        endTime = parse("2018-02-05T19:46:32.101");
        delayInMilliSec = durationMilliSecBetween(startTime, endTime);
        assertThat(delayInMilliSec, is(100D));

        startTime = parse("2018-02-05T19:46:31.631");
        endTime = parse("2018-02-05T19:46:32.731");
        delayInMilliSec = durationMilliSecBetween(startTime, endTime);
        assertThat(delayInMilliSec, is(1100D));

        startTime = parse("2018-02-05T19:45:31.631");
        endTime = parse("2018-02-05T19:46:32.731");
        delayInMilliSec = durationMilliSecBetween(startTime, endTime);
        assertThat(delayInMilliSec, is(61100D));

        startTime = parse("2018-02-05T18:46:31.631");
        endTime = parse("2018-02-05T19:46:32.731");
        delayInMilliSec = durationMilliSecBetween(startTime, endTime);
        assertThat(delayInMilliSec, is(3601100D));

    }
}