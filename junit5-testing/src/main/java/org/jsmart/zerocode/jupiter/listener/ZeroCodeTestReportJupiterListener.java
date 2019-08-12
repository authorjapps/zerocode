package org.jsmart.zerocode.jupiter.listener;

import java.time.LocalDateTime;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeIoWriteBuilder;
import org.jsmart.zerocode.core.logbuilder.ZerocodeCorrelationshipLogger;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;

import static java.time.LocalDateTime.now;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder.newInstance;
import static org.junit.platform.engine.TestExecutionResult.Status.FAILED;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class correlates between request and response of a test and creates easily traceable logs.
 * Here, for each JUnit5 parallel tests this can be interpreted the following way-
 * - a) Request timestamp - When the test started execution
 * - b) Response timestamp - When the test finished execution
 * - c) Response Delay(milli second) - (b-a) milli seconds
 * - d) Scenario: Fully qualified name of the Test-Class
 * - e) Step: Name of the Test-Method in the above Test-Class with @Test(jupiter package)
 *
 * Later the logs are written to the target folder as raw JSON files ready for rendering
 * CSV-reports and Html-Chart/Dashboards
 *
 */
public class ZeroCodeTestReportJupiterListener implements TestExecutionListener {
    private static final Logger LOGGER = getLogger(ZeroCodeTestReportJupiterListener.class);

    private final Class<?> testClass;
    private final String testMethod;
    private String testDescription;

    private ZerocodeCorrelationshipLogger corrLogger;
    private String logPrefixRelationshipId;
    private boolean passed=true;

    public ZeroCodeTestReportJupiterListener(Class<?> testClass, String testMethod) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.testDescription = testClass + "#" + testMethod;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        logPrefixRelationshipId = prepareRequestReport(testDescription);
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        prepareResponseReport(logPrefixRelationshipId);
        buildReportAndPrintToFile(testDescription);
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if(testExecutionResult.getStatus().equals(FAILED)){
            passed = false;
        }
    }

    private String prepareRequestReport(String description) {
        corrLogger = ZerocodeCorrelationshipLogger.newInstance(LOGGER);
        corrLogger.stepLoop(0);
        final String logPrefixRelationshipId = corrLogger.createRelationshipId();
        LocalDateTime timeNow = now();
        corrLogger.aRequestBuilder()
                .stepLoop(0)
                .relationshipId(logPrefixRelationshipId)
                .requestTimeStamp(timeNow)
                .step(testMethod);
        LOGGER.info("JUnit5 *requestTimeStamp:{}, \nJUnit Request:{}", timeNow, logPrefixRelationshipId);
        return logPrefixRelationshipId;
    }

    private void prepareResponseReport(String logPrefixRelationshipId) {
        LocalDateTime timeNow = now();
        LOGGER.info("JUnit5 *responseTimeStamp:{}, \nJUnit Response:{}", timeNow, logPrefixRelationshipId);
        corrLogger.aResponseBuilder()
                .relationshipId(logPrefixRelationshipId)
                .responseTimeStamp(timeNow);
        corrLogger.stepOutcome(passed);
        corrLogger.buildResponseDelay();
    }

    private void buildReportAndPrintToFile(String description) {
        ZeroCodeExecReportBuilder reportResultBuilder = newInstance().loop(0).scenarioName(testClass.getName());
        reportResultBuilder.step(corrLogger.buildReportSingleStep());

        ZeroCodeIoWriteBuilder reportBuilder = ZeroCodeIoWriteBuilder.newInstance().timeStamp(now());
        reportBuilder.result(reportResultBuilder.build());

        reportBuilder.printToFile(description + corrLogger.getCorrelationId() + ".json");
    }

}