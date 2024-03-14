package org.jsmart.zerocode.core.logbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportStepBuilder;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.RESULT_FAIL;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.RESULT_PASS;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TEST_STEP_CORRELATION_ID;
import static org.jsmart.zerocode.core.utils.TokenUtils.getMasksReplaced;

public class ZerocodeCorrelationshipLogger {
    private static final String DISPLAY_DEMARCATION_ = "\n--------- " + TEST_STEP_CORRELATION_ID + " %s ---------";

    private Logger logger;
    private String correlationId;
    private RequestLogBuilder requestLogBuilder = new RequestLogBuilder();
    private ResponseLogBuilder responseLogBuilder = new ResponseLogBuilder();
    private ScenarioLogBuilder scenarioLogBuilder = new ScenarioLogBuilder();
    private Integer stepLoop;
    private Boolean result;
    private Double responseDelay;

    private List<ZeroCodeReportStep> steps = Collections.synchronizedList(new ArrayList());

    public ZerocodeCorrelationshipLogger step(ZeroCodeReportStep step) {
        this.steps.add(step);
        return this;
    }


    public ZerocodeCorrelationshipLogger(Logger logger) {
        this.logger = logger;
    }

    public static ZerocodeCorrelationshipLogger newInstance(Logger logger) {
        return new ZerocodeCorrelationshipLogger(logger);
    }

    public RequestLogBuilder aRequestBuilder() {
        return requestLogBuilder;
    }

    public ZerocodeCorrelationshipLogger assertion(String assertionJson){
        responseLogBuilder.assertionSection(assertionJson);
        return this;
    }

    public ZerocodeCorrelationshipLogger customLog(String customLog){
        responseLogBuilder.customLog(customLog);
        return this;
    }

    public ZerocodeCorrelationshipLogger stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public ZerocodeCorrelationshipLogger stepOutcome(Boolean result) {
        this.result = result;
        return this;
    }

    public ZeroCodeReportStep buildReportSingleStep() {

        result = result != null ? result : false;

        ZeroCodeReportStepBuilder zeroCodeReportStep = ZeroCodeReportStepBuilder.newInstance()
                //.request(requestLogBuilder.request) //TODO
                //.response(responseLogBuilder.response) //TODO
                .loop(stepLoop)
                .name(requestLogBuilder.getStepName())
                .correlationId(getCorrelationId())
                .result(result == true? RESULT_PASS : RESULT_FAIL)
                .url(requestLogBuilder.getUrl())
                .operation(requestLogBuilder.getMethod())
                .assertions(responseLogBuilder.getAssertion())
                .requestTimeStamp(requestLogBuilder.getRequestTimeStamp())
                .responseTimeStamp(responseLogBuilder.responseTimeStamp)
                .responseDelay(responseDelay)
                .id(requestLogBuilder.getId());
        if (this.result) {
        	zeroCodeReportStep.result(RESULT_PASS);
		}else{
			zeroCodeReportStep.response(responseLogBuilder.getResponse());
			zeroCodeReportStep.request(requestLogBuilder.getRequest());
		}
        if(null != responseLogBuilder.customLog){
            zeroCodeReportStep.customLog(responseLogBuilder.customLog);
        }

        return zeroCodeReportStep.build();
    }

    public ResponseLogBuilder aResponseBuilder() {
        return responseLogBuilder;
    }

    public ScenarioLogBuilder aScenarioBuilder() {
        return scenarioLogBuilder;
    }

    public void buildResponseDelay() {
        responseDelay = durationMilliSecBetween(
                requestLogBuilder.getRequestTimeStamp(),
                responseLogBuilder.getResponseTimeStamp()
        );
    }

    public static double durationMilliSecBetween(LocalDateTime requestTimeStamp, LocalDateTime responseTimeStamp) {

        Duration dur = Duration.between(requestTimeStamp, responseTimeStamp != null ? responseTimeStamp : now());
        return dur.toMillis();
    }

    public String createRelationshipId() {
        correlationId = getRelationshipUniqueId();
        return format(DISPLAY_DEMARCATION_, correlationId);
    }

    public static String getRelationshipUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void print() {

        buildResponseDelay();

        String customLog = responseLogBuilder.getCustomLog();
        String assertionsWithMaskRemoved = getMasksReplaced(responseLogBuilder.getAssertion());
        logger.warn(format("%s %s \n*Response delay:%s milli-secs \n%s \n%s \n-done-\n",
                requestLogBuilder.toString(),
                responseLogBuilder.toString(),
                responseDelay,
                "---------> Expected Response: <----------\n" + assertionsWithMaskRemoved,
                customLog == null ? "" : "---------> Custom Log: <----------\n" +customLog
                )
        );
    }

}
