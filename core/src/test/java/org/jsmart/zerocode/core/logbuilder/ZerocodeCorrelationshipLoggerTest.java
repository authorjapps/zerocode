package org.jsmart.zerocode.core.logbuilder;

import org.jsmart.zerocode.core.constants.ReportStepCaptureMode;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.jsmart.zerocode.core.logbuilder.ZerocodeCorrelationshipLogger.durationMilliSecBetween;

public class ZerocodeCorrelationshipLoggerTest {

    private static final String SAMPLE_REQUEST = "{\"url\":\"/users/octocat\"}";
    private static final String SAMPLE_RESPONSE = "{\"status\":200}";

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

    @Test
    public void testPassedStepInFailedOnlyModeWillOmitRequestAndResponse() {
        ZeroCodeReportStep reportStep = buildStep(true, ReportStepCaptureMode.FAILED_ONLY);

        assertThat(reportStep.getRequest(), is(nullValue()));
        assertThat(reportStep.getResponse(), is(nullValue()));
    }

    @Test
    public void testPassedStepInAllModeWillIncludeRequestAndResponse() {
        ZeroCodeReportStep reportStep = buildStep(true, ReportStepCaptureMode.ALL);

        assertThat(reportStep.getRequest(), is(SAMPLE_REQUEST));
        assertThat(reportStep.getResponse(), is(SAMPLE_RESPONSE));
    }

    @Test
    public void testFailedStepInFailedOnlyModeWillIncludeRequestAndResponse() {
        ZeroCodeReportStep reportStep = buildStep(false, ReportStepCaptureMode.FAILED_ONLY);

        assertThat(reportStep.getRequest(), is(SAMPLE_REQUEST));
        assertThat(reportStep.getResponse(), is(SAMPLE_RESPONSE));
    }

    @Test
    public void testFailedStepInAllModeWillIncludeRequestAndResponse() {
        ZeroCodeReportStep reportStep = buildStep(false, ReportStepCaptureMode.ALL);

        assertThat(reportStep.getRequest(), is(SAMPLE_REQUEST));
        assertThat(reportStep.getResponse(), is(SAMPLE_RESPONSE));
    }

    private ZeroCodeReportStep buildStep(boolean stepPassed, ReportStepCaptureMode reportStepCaptureMode) {
        ZerocodeCorrelationshipLogger correlationshipLogger = ZerocodeCorrelationshipLogger
                .newInstance(LoggerFactory.getLogger(ZerocodeCorrelationshipLoggerTest.class))
                .stepCaptureMode(reportStepCaptureMode)
                .stepOutcome(stepPassed);
        correlationshipLogger.aRequestBuilder()
                .request(SAMPLE_REQUEST)
                .requestTimeStamp(parse("2018-02-05T19:46:32.001"));
        correlationshipLogger.aResponseBuilder()
                .response(SAMPLE_RESPONSE)
                .responseTimeStamp(parse("2018-02-05T19:46:32.101"));
        correlationshipLogger.buildResponseDelay();
        return correlationshipLogger.buildReportSingleStep();
    }
}
