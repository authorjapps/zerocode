package org.jsmart.zerocode.core.logbuilder;

import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportStepBuilder;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.RESULT_FAIL;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.RESULT_PASS;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TEST_STEP_CORRELATION_ID;

public class LogCorrelationshipPrinter {
    private static final String DISPLAY_DEMARCATION_ = "\n--------- " + TEST_STEP_CORRELATION_ID + " %s ---------";

    Logger logger;
    static String correlationId;
    RequestLogBuilder requestLogBuilder = new RequestLogBuilder();
    ResponseLogBuilder responseLogBuilder = new ResponseLogBuilder();
    ScenarioLogBuilder scenarioLogBuilder = new ScenarioLogBuilder();
    Integer stepLoop;
    private Boolean result;
    private Double responseDelay;

    public LogCorrelationshipPrinter(Logger logger) {
        this.logger = logger;
    }

    public static LogCorrelationshipPrinter newInstance(Logger logger) {
        return new LogCorrelationshipPrinter(logger);
    }

    public RequestLogBuilder aRequestBuilder() {
        return requestLogBuilder;
    }

    public LogCorrelationshipPrinter assertion(String assertionJson){
        responseLogBuilder.assertionSection(assertionJson);
        return this;
    }

    public LogCorrelationshipPrinter stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public LogCorrelationshipPrinter result(Boolean result) {
        this.result = result;
        return this;
    }

    public ZeroCodeReportStep buildReportSingleStep() {

        result = result != null ? result : false;

        ZeroCodeReportStep zeroCodeReportStep = ZeroCodeReportStepBuilder.newInstance()
                //.request(requestLogBuilder.request) //TODO
                //.response(responseLogBuilder.response) //TODO
                //.assertions()
                .loop(stepLoop)
                .name(requestLogBuilder.stepName)
                .correlationId(correlationId)
                .result(result == true? RESULT_PASS : RESULT_FAIL)
                .url(requestLogBuilder.url)
                .operation(requestLogBuilder.method)
                .requestTimeStamp(requestLogBuilder.requestTimeStamp)
                .responseTimeStamp(responseLogBuilder.responseTimeStamp)
                .responseDelay(responseDelay)
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

        responseDelay = durationMilliSecBetween(
                requestLogBuilder.getRequestTimeStamp(),
                responseLogBuilder.getResponseTimeStamp()
        );

        logger.info(format("%s %s \n*Response delay:%s milli-secs \n%s \n-done-\n",
                requestLogBuilder.toString(),
                responseLogBuilder.toString(),
                responseDelay,
                "---------> Assertion: <----------\n" + responseLogBuilder.getAssertion()
                )
        );

    }

    public static double durationMilliSecBetween(LocalDateTime requestTimeStamp, LocalDateTime responseTimeStamp) {

        Duration dur = Duration.between(requestTimeStamp, responseTimeStamp != null ? responseTimeStamp : now());
        return dur.toMillis();
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
