package org.jsmart.zerocode.core.logbuilder;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeReportStepBuilder;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.UUID;

import static java.lang.String.format;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.RESULT_FAIL;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.RESULT_PASS;

public class LogCorelationshipPrinter {
    private static final String DISPLAY_DEMARCATION_ = "\n--------- RELATIONSHIP-ID: %s ---------";

    Logger logger;
    static String correlationId;
    RequestLogBuilder requestLogBuilder = new RequestLogBuilder();
    ResponseLogBuilder responseLogBuilder = new ResponseLogBuilder();
    ScenarioLogBuilder scenarioLogBuilder = new ScenarioLogBuilder();
    Integer stepLoop;
    private Boolean result;

    public LogCorelationshipPrinter(Logger logger) {
        this.logger = logger;
    }

    public static LogCorelationshipPrinter newInstance(Logger logger) {
        return new LogCorelationshipPrinter(logger);
    }

    public RequestLogBuilder aRequestBuilder() {
        return requestLogBuilder;
    }

    public LogCorelationshipPrinter assertion(String assertionJson){
        responseLogBuilder.assertionSection(assertionJson);
        return this;
    }

    public LogCorelationshipPrinter stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public LogCorelationshipPrinter result(Boolean passed) {
        this.result = passed;
        return this;
    }

    public ZeroCodeReportStep buildReportSingleStep() {

        ZeroCodeReportStep zeroCodeReportStep = ZeroCodeReportStepBuilder.newInstance()
                .loop(stepLoop)
                .name(requestLogBuilder.stepName)
                .correlationId(correlationId)
                .result(result == true? RESULT_PASS : RESULT_FAIL)
                //.requestTimeStamp(requestLogBuilder.requestTimeStamp)
                .build();

        return zeroCodeReportStep;
    }

    public ResponseLogBuilder aResponseBuilder() {
        return responseLogBuilder;
    }

    public ScenarioLogBuilder aScenarioBuilder() {
        return scenarioLogBuilder;
    }

    public void print() {

        logger.info(format("%s %s \n*Response delay:%s milli-secs \n%s \n-done-\n",
                requestLogBuilder.toString(),
                responseLogBuilder.toString(),
                Duration.between(
                        requestLogBuilder.getRequestTimeStamp(),
                        responseLogBuilder.getResponseTimeStamp())

                        /*
                         * 1000000D: Without D it does a integer division and the precision is lost
                         * Note: Java does not have a get(millisec-tem[poral) as of now, only nano
                         * or sec precision is supported
                         */
                        .getNano()/1000000D,
                "---------> Assertion: <----------\n" + responseLogBuilder.getAssertion()
                )
        );

    }

    public static String createRelationshipId() {
        correlationId = getRelationshipUniqueId();
        return format(DISPLAY_DEMARCATION_, correlationId);
    }

    public static String getRelationshipUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String getCorrelationId() {
        return correlationId;
    }



}
