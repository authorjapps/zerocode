package org.jsmart.zerocode.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.engine.assertion.AssertionReport;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutor;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.StepExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesor;
import org.jsmart.zerocode.core.logbuilder.LogCorrelationshipPrinter;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.jsmart.zerocode.core.domain.ZerocodeConstants.*;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder.newInstance;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.wireMockServer;
import static org.jsmart.zerocode.core.utils.ServiceTypeUtils.serviceType;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getFullyQualifiedUrl;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ZeroCodeMultiStepsScenarioRunnerImpl implements ZeroCodeMultiStepsScenarioRunner {

    private static final Logger LOGGER = getLogger(ZeroCodeMultiStepsScenarioRunnerImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Inject
    private ZeroCodeJsonTestProcesor zeroCodeJsonTestProcesor;

    @Inject
    private ZeroCodeExternalFileProcessor extFileProcessor;

    @Inject
    private JsonServiceExecutor serviceExecutor;

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

    private LogCorrelationshipPrinter logCorrelationshipPrinter;

    private static StepNotificationHandler notificationHandler = new StepNotificationHandler();

    private ZeroCodeReportBuilder reportBuilder;

    private ZeroCodeExecResultBuilder reportResultBuilder;

    private Boolean stepOutcomeGreen;

    @Override
    public synchronized boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

        LOGGER.info("\n-------------------------- BDD: Scenario:{} -------------------------\n", scenario.getScenarioName());

        reportBuilder = ZeroCodeReportBuilder.newInstance().timeStamp(LocalDateTime.now());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        final int scenarioLoopTimes = scenario.getLoop() == null ? 1 : scenario.getLoop();

        for (int k = 0; k < scenarioLoopTimes; k++) {

            LOGGER.info("\n### Executing Scenario -->> Count No: " + k);

            /*
             * Build Report scenario for each k
             */
            reportResultBuilder = newInstance()
                    .loop(k)
                    .scenarioName(scenario.getScenarioName());

            for (Step thisStep : scenario.getSteps()) {

                final int stepLoopTimes = thisStep.getLoop() == null ? 1 : thisStep.getLoop();
                for (int i = 0; i < stepLoopTimes; i++) {
                    LOGGER.info("\n### Executing Step -->> Count No: " + i);
                    logCorrelationshipPrinter = LogCorrelationshipPrinter.newInstance(LOGGER);
                    logCorrelationshipPrinter.stepLoop(i);

                    thisStep = extFileProcessor.resolveExtJsonFile(thisStep);

                    String stepId = thisStep.getId();

                    thisStep = createFromStepFile(thisStep, stepId);

                    final String requestJsonAsString = thisStep.getRequest().toString();

                    StepExecutionState stepExecutionState = new StepExecutionState();

                    /*
                     * Create the step name as it is for the 1st stepLoopTimes i.e. i=0.
                     * Rest of the loops suffix as i ie stepName1, stepName2, stepName3 etc
                     */
                    final String thisStepName = thisStep.getName() + (i == 0 ? "" : i);

                    stepExecutionState.addStep(thisStepName);

                    String resolvedRequestJson = zeroCodeJsonTestProcesor.resolveStringJson(
                            requestJsonAsString,
                            scenarioExecutionState.getResolvedScenarioState()
                    );
                    stepExecutionState.addRequest(resolvedRequestJson);

                    String executionResult = "-response not decided-";

                    final String logPrefixRelationshipId = logCorrelationshipPrinter.createRelationshipId();

                    try {
                        String serviceName = thisStep.getUrl();
                        String operationName = thisStep.getOperation();

                        // --------------------------------
                        // Resolve the URL patterns if any
                        // --------------------------------
                        serviceName = zeroCodeJsonTestProcesor.resolveStringJson(
                                serviceName,
                                scenarioExecutionState.getResolvedScenarioState()
                        );

                        final LocalDateTime requestTimeStamp = LocalDateTime.now();
                        switch (serviceType(serviceName, operationName)) {
                            case REST_CALL:
                                serviceName = getFullyQualifiedUrl(serviceName, host, port, applicationContext);
                                logCorrelationshipPrinter.aRequestBuilder()
                                        .stepLoop(i)
                                        .relationshipId(logPrefixRelationshipId)
                                        .requestTimeStamp(requestTimeStamp)
                                        .step(thisStepName)
                                        .url(serviceName)
                                        .method(operationName)
                                        .id(stepId)
                                        .request(prettyPrintJson(resolvedRequestJson));

                                executionResult = serviceExecutor.executeRESTService(serviceName, operationName, resolvedRequestJson);
                                break;

                            case JAVA_CALL:
                                logCorrelationshipPrinter.aRequestBuilder()
                                        .stepLoop(i)
                                        .relationshipId(logPrefixRelationshipId)
                                        .requestTimeStamp(requestTimeStamp)
                                        .step(thisStepName)
                                        .id(stepId)
                                        .url(serviceName)
                                        .method(operationName)
                                        .request(prettyPrintJson(resolvedRequestJson));

                                executionResult = serviceExecutor.executeJavaService(serviceName, operationName, resolvedRequestJson);
                                break;

                            case KAFKA_CALL:
                                if (kafkaServers == null) {
                                    throw new RuntimeException(">>> 'kafka.bootstrap.servers' property can not be null for kafka operations");
                                }
                                printBrokerProperties();
                                logCorrelationshipPrinter.aRequestBuilder()
                                        .stepLoop(i)
                                        .relationshipId(logPrefixRelationshipId)
                                        .requestTimeStamp(requestTimeStamp)
                                        .step(thisStepName)
                                        .url(serviceName)
                                        .method(operationName)
                                        .id(stepId)
                                        .request(prettyPrintJson(resolvedRequestJson));

                                String topicName = serviceName.substring(KAFKA_TOPIC.length());
                                executionResult = serviceExecutor.executeKafkaService(kafkaServers, topicName, operationName, resolvedRequestJson);
                                break;

                            case NONE:
                                logCorrelationshipPrinter.aRequestBuilder()
                                        .stepLoop(i)
                                        .relationshipId(logPrefixRelationshipId)
                                        .requestTimeStamp(requestTimeStamp)
                                        .step(thisStepName)
                                        .id(stepId)
                                        .url(serviceName)
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
                        logCorrelationshipPrinter.aResponseBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .responseTimeStamp(responseTimeStamp)
                                .response(executionResult);

                        stepExecutionState.addResponse(executionResult);
                        scenarioExecutionState.addStepState(stepExecutionState.getResolvedStep());

                        // ---------------------------------
                        // Handle assertion section -START
                        // ---------------------------------
                        String resolvedAssertionJson = zeroCodeJsonTestProcesor.resolveStringJson(
                                thisStep.getAssertions().toString(),
                                scenarioExecutionState.getResolvedScenarioState()
                        );

                        // logging assertion

                        List<JsonAsserter> asserters = zeroCodeJsonTestProcesor.createAssertersFrom(resolvedAssertionJson);
                        List<AssertionReport> failureResults = zeroCodeJsonTestProcesor.assertAllAndReturnFailed(asserters, executionResult);

                        if (!failureResults.isEmpty()) {
                            StringBuilder builder = new StringBuilder();
                            failureResults.forEach(f -> {
                                builder.append(f.toString() + "\n");
                            });
                            logCorrelationshipPrinter.assertion(builder.toString());
                        } else {

                            logCorrelationshipPrinter.assertion(prettyPrintJson(resolvedAssertionJson));
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

                            logCorrelationshipPrinter.result(stepOutcomeGreen);

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

                            logCorrelationshipPrinter.result(stepOutcomeGreen);

                            return stepOutcomeGreen;
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

                        logCorrelationshipPrinter.result(stepOutcomeGreen);

                    } catch (Exception ex) {

                        ex.printStackTrace();
                        LOGGER.info("###Exception while executing a step in the zerocode dsl.");

                        // logging exception message
                        final LocalDateTime responseTimeStampEx = LocalDateTime.now();
                        logCorrelationshipPrinter.aResponseBuilder()
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

                        logCorrelationshipPrinter.result(stepOutcomeGreen);

                        return stepOutcomeGreen;

                    } finally {
                        logCorrelationshipPrinter.print();

                        /*
                         * Build step report for each step
                         * Add the report step to the result step list.
                         */
                        reportResultBuilder.step(logCorrelationshipPrinter.buildReportSingleStep());

                        /*
                         * FAILED and Exception reports are generated here
                         */
                        if (!stepOutcomeGreen) {
                            reportBuilder.result(reportResultBuilder.build());
                            reportBuilder.printToFile(scenario.getScenarioName() + logCorrelationshipPrinter.getCorrelationId() + ".json");
                        }
                    }

                } //<-- for each step-stepLoopTimes

            } //<--- steps for-each

            reportBuilder.result(reportResultBuilder.build());

        } //<-- Scenario Loop

        /*
         * Completed executing all steps?
         * Then stop the wiremock server, so that its ready for next scenario.
         */
        stopWireMockServer();

        /*
         * PASSED reports are generated here
         */
        reportBuilder.printToFile(scenario.getScenarioName() + logCorrelationshipPrinter.getCorrelationId() + ".json");

        /*
         *  There were no steps to execute and the framework will display the test status as Green than Red.
         *  Red symbolises failure, but nothing has failed here.
         */
        return true;
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

    private String getFullyQualifiedRestUrl(String serviceEndPoint) {

        if (host == null || port == null) {
            throw new RuntimeException("'" + PROPERTY_KEY_HOST + "' or 'port' - can not be null");
        }

        if (applicationContext == null) {
            throw new RuntimeException("'" + PROPERTY_KEY_PORT + "' key must be present even if empty or blank");
        }

        if (serviceEndPoint.startsWith("http://") || serviceEndPoint.startsWith("https://")) {

            return serviceEndPoint;

        } else {
            /*
             * Make sure your property file contains context-path with a front slash like "/google-map".
             * -OR-
             * Empty context path is also ok if it requires. In this case do not put a front slash.
             */
            return String.format("%s:%s%s%s", host, port, applicationContext, serviceEndPoint);
        }
    }

    private void stopWireMockServer() {
        if (null != wireMockServer) {
            wireMockServer.stop();
            wireMockServer = null;
            LOGGER.info("Scenario: All mockings done via WireMock server. Dependant end points executed. Stopped WireMock.");
        }
    }

    private void printBrokerProperties() {

        System.out.println("---------------------------------------------------------");
        System.out.println(String.format("kafka.bootstrap.servers - %s", kafkaServers));
        System.out.println("---------------------------------------------------------");

    }

    private Step createFromStepFile(Step thisStep, String stepId) {
        if (thisStep.getStepFile() != null) {
            try {
                thisStep = objectMapper.treeToValue(thisStep.getStepFile(), Step.class);
            } catch (JsonProcessingException e) {
                LOGGER.error("\n### Error while parsing for stepId - {}, stepFile - {}",
                        stepId, thisStep.getStepFile());
                throw new RuntimeException(e);
            }
        }
        return thisStep;
    }
}
