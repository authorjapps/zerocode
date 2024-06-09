package org.jsmart.zerocode.core.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.univocity.parsers.csv.CsvParser;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.jsmart.zerocode.core.constants.ZerocodeConstants.KAFKA_TOPIC;

import org.jsmart.zerocode.core.domain.Parameterized;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder;

import static org.jsmart.zerocode.core.di.provider.CsvParserProvider.LINE_SEPARATOR;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder.newInstance;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeIoWriteBuilder;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutor;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.wireMockServer;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.StepExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeParameterizedProcessor;
import org.jsmart.zerocode.core.engine.sorter.ZeroCodeSorter;
import org.jsmart.zerocode.core.engine.validators.ZeroCodeValidator;
import static org.jsmart.zerocode.core.kafka.helper.KafkaCommonUtils.printBrokerProperties;
import org.jsmart.zerocode.core.logbuilder.ZerocodeCorrelationshipLogger;
import org.jsmart.zerocode.core.utils.ApiTypeUtils;
import static org.jsmart.zerocode.core.utils.ApiTypeUtils.apiType;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getFullyQualifiedUrl;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Arrays;
import java.util.function.BiConsumer;

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
    private ZeroCodeSorter sorter;

    @Inject
    private ApiServiceExecutor apiExecutor;

    @Inject
    private CsvParser csvParser;

    @Inject
    private ApiTypeUtils apiTypeUtils;

    @Inject
    ZeroCodeValidator validator;

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

        LOGGER.warn("\n-----------------------------------------------------------------------------------\n" +
                "\nScenario:\n+++++++++\n\n{} \n" +
                "\n-----------------------------------------------------------------------------------",
                scenario.getScenarioName());

        ioWriterBuilder = ZeroCodeIoWriteBuilder.newInstance().timeStamp(LocalDateTime.now());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        int scenarioLoopTimes = deriveScenarioLoopTimes(scenario);

        boolean wasExecSuccessful = false;

        for (int scnCount = 0; scnCount < scenarioLoopTimes; scnCount++) {

            LOGGER.info("{}\n     Executing Scenario Count No. or parameter No. or Row No. | {} | {}",
                    "\n-------------------------------------------------------------------------",
                    scnCount,
                    "\n-------------------------------------------------------------------------");

            ScenarioSpec parameterizedScenario = parameterizedProcessor.resolveParameterized(scenario, scnCount);

            resultReportBuilder = newInstance()
                    .loop(scnCount)
                    .scenarioName(parameterizedScenario.getScenarioName());

            wasExecSuccessful = executeSteps(notifier, description, scenarioExecutionState, parameterizedScenario);

            ioWriterBuilder.result(resultReportBuilder.build());
        }

        stopIfWireMockServerRunning();

        ioWriterBuilder.printToFile(scenario.getScenarioName() + correlLogger.getCorrelationId() + ".json");

        if (wasExecSuccessful) {
            return stepOutcomeGreen;
        }

        /*
         *  There were no steps to execute and the framework will display the test status as Green than Red.
         *  Red symbolises failure, but nothing has failed here.
         *
         *  Note-This behaviour can be changed on user demands by doing 'return false'.
         */
        return true;
    }

    private boolean executeSteps(RunNotifier notifier,
                                 Description description,
                                 ScenarioExecutionState scenarioExecutionState,
                                 ScenarioSpec parameterizedScenario) {

        ScenarioSpec scenario = parameterizedScenario;

        for (Step thisStep : parameterizedScenario.getSteps()) {
            if (thisStep.getIgnoreStep()) {
                LOGGER.info("Step \"" + thisStep.getName() + "\" is ignored because of ignoreStep property.");
                continue;
            }

            correlLogger = ZerocodeCorrelationshipLogger.newInstance(LOGGER);

            Boolean wasExecSuccess = executeRetryWithSteps(notifier, description, scenarioExecutionState, scenario, thisStep);
            if (wasExecSuccess != null) return wasExecSuccess;
        }

        return false;
    }

    private Boolean executeRetryWithSteps(RunNotifier notifier,
                                          Description description,
                                          ScenarioExecutionState scenarioExecutionState,
                                          ScenarioSpec scenario, Step thisStep) {
        thisStep = extFileProcessor.resolveExtJsonFile(thisStep);
        thisStep = zeroCodeAssertionsProcessor.resolveJsonContent(thisStep, scenarioExecutionState);

        List<Step> thisSteps = extFileProcessor.createFromStepFile(thisStep, thisStep.getId());
        if(null == thisSteps || thisSteps.isEmpty()) thisSteps.add(thisStep);
        Boolean wasExecSuccess = null;
        for(Step step : thisSteps) {
             wasExecSuccess = executeRetry(notifier,
                    description,
                    scenarioExecutionState,
                    scenario,
                    step);
            if (wasExecSuccess != null) {
                return wasExecSuccess;
            }
        }
        return null;
    }

    private Boolean executeRetry(RunNotifier notifier,
                                 Description description,
                                 ScenarioExecutionState scenarioExecutionState,
                                 ScenarioSpec scenario,
                                 Step thisStep) {

        final String logPrefixRelationshipId = correlLogger.createRelationshipId();
        String executionResult = "-response unavailable-";

        // --------------------------------------
        // Save step execution state in a context
        // --------------------------------------
        final String requestJsonAsString = Optional.ofNullable(thisStep.getRequest()).orElse(NullNode.getInstance()).toString();
        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(thisStep);
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
            if(retryCounter > 0){
                LOGGER.warn("\n\n------------>Retrying...[step][attempt-{}][executions-{}]:'{}' -> '{}'",
                        retryCounter, (retryCounter+1), scenario.getScenarioName(), thisStep.getName());
            }
            try {
                if (retryCounter > 0 && !isEmpty(thisStep.getRetry().getWithSteps())) {
                    for (String stepName : thisStep.getRetry().getWithSteps()) {
                        Optional<StepExecutionState> retryWithStepExecState = scenarioExecutionState.getExecutedStepState(stepName);
                        if (!retryWithStepExecState.isPresent()) {
                            throw new RuntimeException("Invalid step to retry with : " + stepName + " has not been executed yet");
                        }
                        executeRetry(notifier, description, scenarioExecutionState, scenario, retryWithStepExecState.get().getStep());
                    }
                }
                executionResult = executeApi(logPrefixRelationshipId, thisStep, resolvedRequestJson, scenarioExecutionState);

                // logging response
                final LocalDateTime responseTimeStamp = LocalDateTime.now();
                correlLogger.aResponseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(responseTimeStamp)
                        .response(executionResult);
                correlLogger.aResponseBuilder().customLog(thisStep.getCustomLog());
                stepExecutionState.addResponse(executionResult);
                scenarioExecutionState.addStepState(stepExecutionState);

                // ---------------------------------
                // Handle sort section
                // ---------------------------------
                if (!Objects.isNull(thisStep.getSort())) {
                    executionResult = sorter.sortArrayAndReplaceInResponse(thisStep, executionResult, scenarioExecutionState.getResolvedScenarioState());
                    correlLogger.customLog("Updated response: " + executionResult);
                    stepExecutionState.addResponse(executionResult);
                    scenarioExecutionState.addStepState(stepExecutionState);
                }

                // ---------------------------------
                // Handle assertion section -START
                // ---------------------------------
                String resolvedAssertionJson = zeroCodeAssertionsProcessor.resolveStringJson(
                        Optional.ofNullable(thisStep.getAssertions()).orElse(NullNode.getInstance()).toString(),
                        scenarioExecutionState.getResolvedScenarioState()
                );

                // -----------------
                // logging assertion
                // -----------------
                List<FieldAssertionMatcher> failureResults = compareStepResults(thisStep, executionResult, resolvedAssertionJson, scenarioExecutionState.getResolvedScenarioState());

                if (!failureResults.isEmpty()) {
                    StringBuilder builder = new StringBuilder();

                    // Print expected Payload along with assertion errors
                    builder.append("Assumed Payload: \n").append(prettyPrintJson(resolvedAssertionJson)).append("\n");
                    builder.append("Assertion Errors: \n");

                    failureResults.forEach(f -> {
                        builder.append(f.toString()).append("\n");
                    });
                    correlLogger.assertion(resolvedAssertionJson != null ? builder.toString() : expectedValidatorsAsJson(thisStep));
                } else {
                    correlLogger.assertion(resolvedAssertionJson != null && !"null".equalsIgnoreCase(resolvedAssertionJson) ?
                            prettyPrintJson(resolvedAssertionJson) : expectedValidatorsAsJson(thisStep));
                }

                if (retryTillSuccess && (retryCounter + 1 < retryMaxTimes) && !failureResults.isEmpty()) {
                    LOGGER.info("\n---------------------------------------\n" +
                            "        Retry: Attempt number: {}", retryCounter + 2 +
                            "\n---------------------------------------\n");
                    waitForDelay(retryDelay);

                    // Set stepOutcomeGreen to true - Not to write report at finally with printToFile().
                    stepOutcomeGreen = true;
                    continue;
                }

                boolean ignoreStepFailures = scenario.getIgnoreStepFailures() == null ? false : scenario.getIgnoreStepFailures();
                if (!failureResults.isEmpty()) {
                    stepOutcomeGreen = notificationHandler.handleAssertion(
                            notifier,
                            description,
                            scenario.getScenarioName(),
                            thisStepName,
                            failureResults,
                            notificationHandler::handleAssertionFailed);

                    correlLogger.stepOutcome(stepOutcomeGreen);

                    if (ignoreStepFailures == true) {
                        // ---------------------------------------------------------------------
                        // Make it Green so that the report doesn't get generated again,
                        // in the finally block i.e. printToFile. Once the scenario completes
                        // execution, all reports(passed n failed) wriitten to file at once.
                        // ---------------------------------------------------------------------
                        stepOutcomeGreen = true;

                        // Do not stop execution. Force-continue to the next step
                        continue;
                    }

                    return true;
                }

                // -----------------
                // Handle PASS cases
                // -----------------
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

                correlLogger.stepOutcome(stepOutcomeGreen);

                if (retryTillSuccess) {
                    LOGGER.info("Retry: Leaving early with successful assertion");
                    break;
                }

            } catch (Exception ex) {

                ex.printStackTrace();
                LOGGER.error("###Exception while executing a step in the zerocode dsl.");

                // logging exception message
                final LocalDateTime responseTimeStampEx = LocalDateTime.now();
                correlLogger.aResponseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(responseTimeStampEx)
                        .response(executionResult)
                        .exceptionMessage(ex.getMessage());

                // Step threw an exception. Handle Exception cases
                stepOutcomeGreen = notificationHandler.handleAssertion(
                        notifier,
                        description,
                        scenario.getScenarioName(),
                        thisStepName,
                        (new RuntimeException("ZeroCode Step execution failed. Details:" + ex)),
                        notificationHandler::handleStepException);

                correlLogger.stepOutcome(stepOutcomeGreen);

                return true;

            } finally {
                correlLogger.print();

                // Build step report for each step. Add the report step to the step list.
                resultReportBuilder.step(correlLogger.buildReportSingleStep());

                /*
                 * FAILED and Exception reports are generated here
                 * TODO- Remove this block in the future release - After testing exception cases
                 */
                if (!stepOutcomeGreen) {
                    //ioWriterBuilder.result(resultReportBuilder.build());
                    //ioWriterBuilder.printToFile(scenario.getScenarioName() + correlLogger.getCorrelationId() + ".json");
                }
            }
        }

        return null;
    }

    private String expectedValidatorsAsJson(Step thisStep) throws JsonProcessingException {
        if(thisStep.getValidators() == null){
            return "No validators were found for this step";
        }
        return prettyPrintJson(objectMapper.writeValueAsString((thisStep.getValidators())));
    }

    private String executeApi(String logPrefixRelationshipId,
                              Step thisStep,
                              String resolvedRequestJson,
                              ScenarioExecutionState scenarioExecutionState) {

        String url = thisStep.getUrl();
        String operationName = thisStep.getOperation();
        String stepId = thisStep.getId();
        String thisStepName = thisStep.getName();

        // --------------------------------
        // Resolve the URL patterns if any
        // --------------------------------
        url = zeroCodeAssertionsProcessor.resolveStringJson(url, scenarioExecutionState.getResolvedScenarioState());

        // ------------------------------------------------
        // 1) Removed the MASKED wrapper for API execution (For logging)
        // 2) Replace the MASKED field with masked content (For API executions)
        // ------------------------------------------------
        String resolvedRequestJsonMaskRemoved = zeroCodeAssertionsProcessor.fieldMasksRemoved(resolvedRequestJson);
        String resolvedRequestJsonMaskApplied = zeroCodeAssertionsProcessor.fieldMasksApplied(resolvedRequestJson);

        final LocalDateTime requestTimeStamp = LocalDateTime.now();

        String executionResult;

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
                        .request(prettyPrintJson(resolvedRequestJsonMaskApplied));

                executionResult = apiExecutor.executeHttpApi(url, operationName, resolvedRequestJsonMaskRemoved);
                break;

            case JAVA_CALL:
                correlLogger.aRequestBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .requestTimeStamp(requestTimeStamp)
                        .step(thisStepName)
                        .id(stepId)
                        .url(url)
                        .method(operationName)
                        .request(prettyPrintJson(resolvedRequestJsonMaskApplied));

                url = apiTypeUtils.getQualifiedJavaApi(url);
                executionResult = apiExecutor.executeJavaOperation(url, operationName, resolvedRequestJsonMaskRemoved);
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
                        .method(operationName.toUpperCase())
                        .id(stepId)
                        .request(prettyPrintJson(resolvedRequestJsonMaskApplied));

                String topicName = url.substring(KAFKA_TOPIC.length());
                executionResult = apiExecutor.executeKafkaService(kafkaServers, topicName, operationName, resolvedRequestJsonMaskRemoved, scenarioExecutionState);
                break;

            case NONE:
                correlLogger.aRequestBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .requestTimeStamp(requestTimeStamp)
                        .step(thisStepName)
                        .id(stepId)
                        .url(url)
                        .method(operationName)
                        .request(prettyPrintJson(resolvedRequestJsonMaskApplied));

                executionResult = prettyPrintJson(resolvedRequestJsonMaskApplied);
                break;

            default:
                throw new RuntimeException("Oops! API Type Undecided. If it is intentional, " +
                        "then keep the value as empty to receive the request as response");
        }

        return executionResult;
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

    private void stopIfWireMockServerRunning() {
        if (null != wireMockServer) {
            wireMockServer.stop();
            wireMockServer = null;
            LOGGER.debug("Scenario: All mockings done via WireMock server. Dependant end points executed. Stopped WireMock.");
        }
    }

    private int deriveScenarioLoopTimes(ScenarioSpec scenario) {
        int scenarioLoopTimes = scenario.getLoop() == null ? 1 : scenario.getLoop();
        int parameterSize = getParameterSize(scenario.getParameterized());
        scenarioLoopTimes = parameterSize != 0 ? parameterSize : scenarioLoopTimes;
        return scenarioLoopTimes;
    }

    private int getParameterSize(Parameterized parameterized) {
        if (parameterized == null) {
            return 0;
        }

        List<Object> valueSource = parameterized.getValueSource();
        List<String> csvSource = parameterized.getCsvSource();
        int csvSourceSize = 0;

        if (csvSource != null && !csvSource.isEmpty()){
            String[] parsedHeaderLine = csvParser.parseLine(csvSource.get(0) + LINE_SEPARATOR);
            boolean hasHeader = parsedHeaderLine.length > 0 && Arrays.stream(parsedHeaderLine).allMatch(s -> s.matches("^\\|.*\\|$"));
            csvSourceSize = hasHeader ? csvSource.size() -1 : csvSource.size();
        }

        return valueSource != null ? valueSource.size() : csvSourceSize;
    }

    private List<FieldAssertionMatcher> compareStepResults(Step thisStep, String actualResult, String expectedResult, String resolvedScenarioState) {
        List<FieldAssertionMatcher> failureResults = new ArrayList<>();

        expectedResult = zeroCodeAssertionsProcessor.fieldMasksRemoved(expectedResult);

        // --------------------
        //  Validators (pyrest)
        // --------------------
        if (thisStep.getValidators() != null) {
            failureResults = validator.validateFlat(thisStep, actualResult, resolvedScenarioState);
        }

        // ------------------------
        // STRICT mode (skyscreamer)
        // ------------------------
        else if (ofNullable(thisStep.getVerifyMode()).orElse("LENIENT").equals("STRICT")) {
            failureResults = validator.validateStrict(expectedResult, actualResult);
        }

        // --------------------------
        // LENIENT mode (skyscreamer)
        // --------------------------
        else {
            failureResults = validator.validateLenient(expectedResult, actualResult);
        }

        return failureResults;
    }

}
