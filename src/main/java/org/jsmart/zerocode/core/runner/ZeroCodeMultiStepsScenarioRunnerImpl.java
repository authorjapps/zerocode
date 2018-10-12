package org.jsmart.zerocode.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.engine.assertion.AssertionReport;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutor;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.StepExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesor;
import org.jsmart.zerocode.core.logbuilder.LogCorrelationshipPrinter;
import org.jsmart.zerocode.core.utils.ServiceType;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder.newInstance;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.wireMockServer;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ZeroCodeMultiStepsScenarioRunnerImpl implements ZeroCodeMultiStepsScenarioRunner {

    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeMultiStepsScenarioRunnerImpl.class);

    //guice -starts
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ZeroCodeJsonTestProcesor zeroCodeJsonTestProcesor;

    @Inject
    private JsonServiceExecutor serviceExecutor;

    @Inject
    @Named("web.application.endpoint.host")
    private String host;

    @Inject
    @Named("web.application.endpoint.port")
    private String port;

    @Inject
    @Named("web.application.endpoint.context")
    private String applicationContext;
    //guice -ends

    private LogCorrelationshipPrinter logCorrelationshipPrinter;

    private static StepNotificationHandler notificationHandler = new StepNotificationHandler();

    private ZeroCodeReportBuilder reportBuilder = ZeroCodeReportBuilder.newInstance().timeStamp(LocalDateTime.now());

    private ZeroCodeExecResultBuilder reportResultBuilder;

    private Boolean stepOutcome;

    @Override
    public synchronized boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

        LOGGER.info("\n-------------------------- BDD: Scenario:{} -------------------------\n", scenario.getScenarioName());

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
                // Another way to get the String
                // String requestJson = objectMapper.valueToTree(thisStep.getRequest()).toString();

                final int stepLoopTimes = thisStep.getLoop() == null ? 1 : thisStep.getLoop();
                for (int i = 0; i < stepLoopTimes; i++) {
                    LOGGER.info("\n### Executing Step -->> Count No: " + i);
                    logCorrelationshipPrinter = LogCorrelationshipPrinter.newInstance(LOGGER);
                    logCorrelationshipPrinter.stepLoop(i);

                    final String requestJsonAsString = thisStep.getRequest().toString();

                    StepExecutionState stepExecutionState = new StepExecutionState();

                    /*
                     * Create the step name as it is for the 1st stepLoopTimes ie i=0.
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
        
                        // Resolve the URL patterns if any
                        serviceName = zeroCodeJsonTestProcesor.resolveStringJson(
                                        serviceName,
                                        scenarioExecutionState.getResolvedScenarioState()
                                                                                );
        
                        final LocalDateTime requestTimeStamp = LocalDateTime.now();
                        switch (serviceType(serviceName, operationName)) {
                            case REST_CALL:
                                serviceName = getFullyQualifiedRestUrl(serviceName);
                                logCorrelationshipPrinter.aRequestBuilder()
                                                .stepLoop(i)
                                                .relationshipId(logPrefixRelationshipId)
                                                .requestTimeStamp(requestTimeStamp)
                                                .step(thisStepName)
                                                .url(serviceName)
                                                .method(operationName)
                                                .request(prettyPrintJson(resolvedRequestJson));
                
                                executionResult = serviceExecutor.executeRESTService(serviceName, operationName, resolvedRequestJson);
                                break;
            
                            case JAVA_CALL:
                                logCorrelationshipPrinter.aRequestBuilder()
                                                .stepLoop(i)
                                                .relationshipId(logPrefixRelationshipId)
                                                .requestTimeStamp(requestTimeStamp)
                                                .step(thisStepName)
                                                .url(serviceName)
                                                .method(operationName)
                                                .request(prettyPrintJson(resolvedRequestJson));
                
                                executionResult = serviceExecutor.executeJavaService(serviceName, operationName, resolvedRequestJson);
                                break;
            
                            case NONE:
                                logCorrelationshipPrinter.aRequestBuilder()
                                                .stepLoop(i)
                                                .relationshipId(logPrefixRelationshipId)
                                                .requestTimeStamp(requestTimeStamp)
                                                .step(thisStepName)
                                                .url(serviceName)
                                                .method(operationName)
                                                .request(prettyPrintJson(resolvedRequestJson));
                
                                executionResult = prettyPrintJson(resolvedRequestJson);
                                break;
            
                            default:
                                throw new RuntimeException("Opps! Service Undecided. If it is intentional, then leave it blank for same response as request");
                        }

                        // logging response
                        final LocalDateTime responseTimeStamp = LocalDateTime.now();
                        logCorrelationshipPrinter.aResponseBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .responseTimeStamp(responseTimeStamp)
                                .response(executionResult);

                        stepExecutionState.addResponse(executionResult);
                        scenarioExecutionState.addStepState(stepExecutionState.getResolvedStep());

                        /** ******************************
                         * Handle assertion section -START
                         * *******************************
                         */
                        String resolvedAssertionJson = zeroCodeJsonTestProcesor.resolveStringJson(
                                thisStep.getAssertions().toString(),
                                scenarioExecutionState.getResolvedScenarioState()
                        );

                        // logging assertion
                        logCorrelationshipPrinter.assertion(prettyPrintJson(resolvedAssertionJson));

                        List<JsonAsserter> asserters = zeroCodeJsonTestProcesor.createAssertersFrom(resolvedAssertionJson);
                        List<AssertionReport> failureResults = zeroCodeJsonTestProcesor.assertAllAndReturnFailed(asserters, executionResult);

                        if (!failureResults.isEmpty()) {
                            /*
                             * Step failed
                             */
                            stepOutcome = notificationHandler.handleAssertion(
                                    notifier,
                                    description,
                                    scenario.getScenarioName(),
                                    thisStepName,
                                    failureResults,
                                    notificationHandler::handleAssertionFailed);

                            logCorrelationshipPrinter.result(stepOutcome);

                            return stepOutcome;
                        }

                        /*
                         * Test step stepOutcome
                         */
                        stepOutcome = notificationHandler.handleAssertion(
                                notifier,
                                description,
                                scenario.getScenarioName(),
                                thisStepName,
                                failureResults,
                                notificationHandler::handleAssertionPassed);

                        /** ******************************
                         * Handle assertion section   -END
                         * *******************************
                         */

                        logCorrelationshipPrinter.result(stepOutcome);

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
                        stepOutcome = notificationHandler.handleAssertion(
                                notifier,
                                description,
                                scenario.getScenarioName(),
                                thisStepName,
                                (new RuntimeException("ZeroCode Step execution failed. Details:" + ex)),
                                notificationHandler::handleStepException);

                        logCorrelationshipPrinter.result(stepOutcome);

                        return stepOutcome;

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
                        if(!stepOutcome) {
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
    
    private ServiceType serviceType(String serviceName, String methodName) {
        ServiceType serviceType;
        
        if (StringUtils.isEmpty(serviceName) || isEmpty(methodName)) {
            serviceType = ServiceType.NONE;
        } else if (serviceName != null && serviceName.contains("/")) {
            serviceType = ServiceType.REST_CALL;
        } else {
            serviceType = ServiceType.JAVA_CALL;
        }
        
        return serviceType;
    }

    private String getFullyQualifiedRestUrl(String serviceEndPoint) {
        if (serviceEndPoint.startsWith("http://") || serviceEndPoint.startsWith("https://")) {
            return serviceEndPoint;
        } else {
            /*
             * Make sure your property file contains context-path with a front slash like "/google-map".
             * -OR-
             * Empty context path is also ok if it requires. In this case dont put front slash.
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
}
