package org.jsmart.zerocode.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.univocity.parsers.csv.CsvParser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import org.jsmart.zerocode.core.domain.Parameterized;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeIoWriteBuilder;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutor;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.StepExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeParameterizedProcessor;
import org.jsmart.zerocode.core.logbuilder.ZerocodeCorrelationshipLogger;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;

import static org.jsmart.zerocode.core.domain.ZerocodeConstants.KAFKA_TOPIC;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder.newInstance;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.wireMockServer;
import static org.jsmart.zerocode.core.kafka.helper.KafkaCommonUtils.printBrokerProperties;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getFullyQualifiedUrl;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getParameterSize;
import static org.jsmart.zerocode.core.utils.ServiceTypeUtils.apiType;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ZeroCodeMultiStepsScenarioRunnerImpl implements ZeroCodeMultiStepsScenarioRunner {

    private static final Logger LOGGER = getLogger(ZeroCodeMultiStepsScenarioRunnerImpl.class);

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor;

    @Inject
    private ZeroCodeExternalFileProcessor extFileProcessor;

    @Inject
    private ZeroCodeParameterizedProcessor parameterizedProcessor;

    @Inject
    private ApiServiceExecutor apiExecutor;

    @Inject
    private CsvParser csvParser;

    @Inject(optional = true)
    @Named("web.application.endpoint.host")
    private String host;

    @Inject(optional = true)
    @Named("web.application.endpoint.port")
    private String port;

    @Inject(optional = true)
    @Named("web.application.endpoint.context")
    private String applicationContext;

    @Inject(optional = true)
    @Named("kafka.bootstrap.servers")
    private String kafkaServers;

    //guice -ends

    private ZerocodeCorrelationshipLogger correlLogger;

    private static StepNotificationHandler notificationHandler = new StepNotificationHandler();

    private ZeroCodeIoWriteBuilder ioWriterBuilder;

    private ZeroCodeExecReportBuilder resultReportBuilder;

    private Boolean stepOutcomeGreen;

    @Override
    public synchronized boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

        LOGGER.info("\n-------------------------- BDD: Scenario:{} -------------------------\n", scenario.getScenarioName());

        ioWriterBuilder = ZeroCodeIoWriteBuilder.newInstance().timeStamp(LocalDateTime.now());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        int scenarioLoopTimes = deriveScenarioLoopTimes(scenario);

        for (int scnCount = 0; scnCount < scenarioLoopTimes; scnCount++) {

            LOGGER.info("{}\n     Executing Scenario Count No. or parameter No. or Row No. | {} | {}",
                    "\n-------------------------------------------------------------------------",
                    scnCount,
                    "\n-------------------------------------------------------------------------");

            ScenarioSpec parameterizedScenario = parameterizedProcessor.resolveParameterized(scenario, scnCount);

            // ---------------------------------------
            // Build Report scenario for each scnCount
            // ---------------------------------------
            resultReportBuilder = newInstance()
                    .loop(scnCount)
                    .scenarioName(parameterizedScenario.getScenarioName());

            boolean didPass = executeSteps(notifier, description, scenarioExecutionState, parameterizedScenario);

            if (didPass) {
                return stepOutcomeGreen;
            }

            ioWriterBuilder.result(resultReportBuilder.build());

        }

        /*
         * Completed executing all steps?
         * Then stop the wiremock server, so that its ready for next scenario.
         */
        stopWireMockServer();

        /*
         * PASSED reports are generated here
         */
        ioWriterBuilder.printToFile(scenario.getScenarioName() + correlLogger.getCorrelationId() + ".json");

        /*
         *  There were no steps to execute and the framework will display the test status as Green than Red.
         *  Red symbolises failure, but nothing has failed here.
         */
        return true;
    }

    private boolean executeSteps(RunNotifier notifier,
                                 Description description,
                                 ScenarioExecutionState scenarioExecutionState,
                                 ScenarioSpec parameterizedScenario) {

        ScenarioSpec scenario = parameterizedScenario;

        for (Step thisStep : parameterizedScenario.getSteps()) {

            correlLogger = ZerocodeCorrelationshipLogger.newInstance(LOGGER);

            thisStep = extFileProcessor.resolveExtJsonFile(thisStep);
            thisStep = extFileProcessor.createFromStepFile(thisStep, thisStep.getId());

            Boolean didPass = executeRetry(notifier,
                    description,
                    scenarioExecutionState,
                    scenario,
                    thisStep);

            if (didPass != null) {
                return didPass;
            }

        }

        return false;
    }

    private Boolean executeRetry(RunNotifier notifier,
                                 Description description,
                                 ScenarioExecutionState scenarioExecutionState,
                                 ScenarioSpec scenario,
                                 Step thisStep) {

        final String logPrefixRelationshipId = correlLogger.createRelationshipId();
        String executionResult = "-response not decided-";
        String stepId = thisStep.getId();

        // --------------------------------------
        // Save step execution state in a context
        // --------------------------------------
        final String requestJsonAsString = thisStep.getRequest().toString();
        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(thisStep.getName());
        String resolvedRequestJson = zeroCodeAssertionsProcessor.resolveStringJson(
                requestJsonAsString,
                scenarioExecutionState.getResolvedScenarioState());
        stepExecutionState.addRequest(resolvedRequestJson);

        // -----------------------
        // Handle retry mechanism
        // -----------------------
        boolean retryTillSuccess = false;
        int retryDelay = 0;
        int retryMaxTimes = 1;
        if (thisStep.getRetry() != null) {
            retryMaxTimes = thisStep.getRetry().getMax();
            retryDelay = thisStep.getRetry().getDelay();
            retryTillSuccess = true;
        }

        String thisStepName = thisStep.getName();

        for (int retryCounter = 0; retryCounter < retryMaxTimes; retryCounter++) {
            try {
                String url = thisStep.getUrl();
                String operationName = thisStep.getOperation();

                // --------------------------------
                // Resolve the URL patterns if any
                // --------------------------------
                url = zeroCodeAssertionsProcessor.resolveStringJson(
                        url,
                        scenarioExecutionState.getResolvedScenarioState()
                );

                final LocalDateTime requestTimeStamp = LocalDateTime.now();
                switch (apiType(url, operationName)) {
                    case REST_CALL:
                        url = getFullyQualifiedUrl(url, host, port, applicationContext);
                        correlLogger.aRequestBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .requestTimeStamp(requestTimeStamp)
                                .step(thisStepName)
                                .url(url)
                                .method(operationName)
                                .id(stepId)
                                .request(prettyPrintJson(resolvedRequestJson));

                        executionResult = apiExecutor.executeHttpApi(url, operationName, resolvedRequestJson);
                        break;

                    case JAVA_CALL:
                        correlLogger.aRequestBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .requestTimeStamp(requestTimeStamp)
                                .step(thisStepName)
                                .id(stepId)
                                .url(url)
                                .method(operationName)
                                .request(prettyPrintJson(resolvedRequestJson));

                        executionResult = apiExecutor.executeJavaOperation(url, operationName, resolvedRequestJson);
                        break;

                    case KAFKA_CALL:
                        if (kafkaServers == null) {
                            throw new RuntimeException(">>> 'kafka.bootstrap.servers' property can not be null for kafka operations");
                        }
                        printBrokerProperties(kafkaServers);
                        correlLogger.aRequestBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .requestTimeStamp(requestTimeStamp)
                                .step(thisStepName)
                                .url(url)
                                .method(operationName)
                                .id(stepId)
                                .request(prettyPrintJson(resolvedRequestJson));

                        String topicName = url.substring(KAFKA_TOPIC.length());
                        executionResult = apiExecutor.executeKafkaService(kafkaServers, topicName, operationName, resolvedRequestJson);
                        break;

                    case NONE:
                        correlLogger.aRequestBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .requestTimeStamp(requestTimeStamp)
                                .step(thisStepName)
                                .id(stepId)
                                .url(url)
                                .method(operationName)
                                .request(prettyPrintJson(resolvedRequestJson));

                        executionResult = prettyPrintJson(resolvedRequestJson);
                        break;

                    default:
                        throw new RuntimeException("Oops! Service Type Undecided. If it is intentional, " +
                                "then keep the value as empty to receive the request in the response");
                }

                // logging response
                final LocalDateTime responseTimeStamp = LocalDateTime.now();
                correlLogger.aResponseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(responseTimeStamp)
                        .response(executionResult);

                stepExecutionState.addResponse(executionResult);
                scenarioExecutionState.addStepState(stepExecutionState.getResolvedStep());

                // ---------------------------------
                // Handle assertion section -START
                // ---------------------------------
                String resolvedAssertionJson = zeroCodeAssertionsProcessor.resolveStringJson(
                        thisStep.getAssertions().toString(),
                        scenarioExecutionState.getResolvedScenarioState()
                );

                // -----------------
                // logging assertion
                // -----------------
                List<JsonAsserter> asserters = zeroCodeAssertionsProcessor.createJsonAsserters(resolvedAssertionJson);
                List<FieldAssertionMatcher> failureResults = zeroCodeAssertionsProcessor.assertAllAndReturnFailed(asserters, executionResult);

                if (!failureResults.isEmpty()) {
                    StringBuilder builder = new StringBuilder();

                    // Print expected Payload along with assertion errors
                    builder.append("Assumed Payload: \n" + prettyPrintJson(resolvedAssertionJson) + "\n");
                    builder.append("Assertion Errors: \n");

                    failureResults.forEach(f -> {
                        builder.append(f.toString() + "\n");
                    });
                    correlLogger.assertion(builder.toString());
                } else {

                    correlLogger.assertion(prettyPrintJson(resolvedAssertionJson));
                }

                if (retryTillSuccess && (retryCounter + 1 < retryMaxTimes) && !failureResults.isEmpty()) {
                    LOGGER.info("\n---------------------------------------\n" +
                            "        Retry: Attempt number: {}", retryCounter + 2 +
                            "\n---------------------------------------\n");
                    waitForDelay(retryDelay);
                    // ---------------------------------------------------------------------
                    // Make it Green so that the report doesn't get generated again in the
                    // finally block i.e. printToFile(). Once the scenario
                    // get executed all reports(passed n failed) printed to file at once
                    // ---------------------------------------------------------------------
                    stepOutcomeGreen = true;
                    continue;
                }
                // --------------------------------------------------------------------------------
                // Non dependent requests into a single JSON file (Issue-167 - Feature Implemented)
                // --------------------------------------------------------------------------------
                boolean ignoreStepFailures = scenario.getIgnoreStepFailures() == null ? false : scenario.getIgnoreStepFailures();
                if (ignoreStepFailures == true && !failureResults.isEmpty()) {
                    stepOutcomeGreen = notificationHandler.handleAssertion(
                            notifier,
                            description,
                            scenario.getScenarioName(),
                            thisStepName,
                            failureResults,
                            notificationHandler::handleAssertionFailed);

                    correlLogger.result(stepOutcomeGreen);

                    // ---------------------------------------------------------------------
                    // Make it Green so that the report doesn't get generated again,
                    // in the finally block i.e. printToFile. Once the scenario
                    // get executed all reports(passed n failed) printed to file at once
                    // ---------------------------------------------------------------------
                    stepOutcomeGreen = true;

                    // ---------------------------------------------------------------------
                    // Do not stop execution after this step.
                    // Continue to the next step after printing/logging the failure report.
                    // ---------------------------------------------------------------------
                    continue;
                }
                if (!failureResults.isEmpty()) {
                    /*
                     * Step failed
                     */
                    stepOutcomeGreen = notificationHandler.handleAssertion(
                            notifier,
                            description,
                            scenario.getScenarioName(),
                            thisStepName,
                            failureResults,
                            notificationHandler::handleAssertionFailed);

                    correlLogger.result(stepOutcomeGreen);

                    return true;
                }

                /*
                 * Test step stepOutcomeGreen
                 */
                stepOutcomeGreen = notificationHandler.handleAssertion(
                        notifier,
                        description,
                        scenario.getScenarioName(),
                        thisStepName,
                        failureResults,
                        notificationHandler::handleAssertionPassed);
                // ---------------------------------
                // Handle assertion section -END
                // ---------------------------------

                correlLogger.result(stepOutcomeGreen);

                if (retryTillSuccess) {
                    LOGGER.info("Retry: Leaving early with successful assertion");
                    break;
                }

            } catch (Exception ex) {

                ex.printStackTrace();
                LOGGER.info("###Exception while executing a step in the zerocode dsl.");

                // logging exception message
                final LocalDateTime responseTimeStampEx = LocalDateTime.now();
                correlLogger.aResponseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(responseTimeStampEx)
                        .response(executionResult)
                        .exceptionMessage(ex.getMessage());

                /*
                 * Step threw an exception
                 */
                stepOutcomeGreen = notificationHandler.handleAssertion(
                        notifier,
                        description,
                        scenario.getScenarioName(),
                        thisStepName,
                        (new RuntimeException("ZeroCode Step execution failed. Details:" + ex)),
                        notificationHandler::handleStepException);

                correlLogger.result(stepOutcomeGreen);

                return true;

            } finally {
                correlLogger.print();

                /*
                 * Build step report for each step
                 * Add the report step to the result step list.
                 */
                resultReportBuilder.step(correlLogger.buildReportSingleStep());

                /*
                 * FAILED and Exception reports are generated here
                 */
                if (!stepOutcomeGreen) {
                    ioWriterBuilder.result(resultReportBuilder.build());
                    ioWriterBuilder.printToFile(scenario.getScenarioName() + correlLogger.getCorrelationId() + ".json");
                }
            }
        }
        return null;
    }

    private void waitForDelay(int delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer testPassHandler) {

        scenarioSpec.getSteps()
                .forEach(step -> testPassHandler.accept(scenarioSpec.getScenarioName(), step.getName()));

        return true;
    }

    public void overridePort(int port) {
        this.port = port + "";
    }

    public void overrideHost(String host) {
        this.host = host;
    }

    public void overrideApplicationContext(String applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void stopWireMockServer() {
        if (null != wireMockServer) {
            wireMockServer.stop();
            wireMockServer = null;
            LOGGER.info("Scenario: All mockings done via WireMock server. Dependant end points executed. Stopped WireMock.");
        }
    }

    private int deriveScenarioLoopTimes(ScenarioSpec scenario) {
        int scenarioLoopTimes = scenario.getLoop() == null ? 1 : scenario.getLoop();
        int parameterSize = getParameterSize(scenario.getParameterized());
        scenarioLoopTimes = parameterSize != 0 ? parameterSize : scenarioLoopTimes;
        return scenarioLoopTimes;
    }
}
