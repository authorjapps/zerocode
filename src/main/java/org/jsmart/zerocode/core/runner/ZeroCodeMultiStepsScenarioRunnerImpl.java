package org.jsmart.zerocode.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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
import org.jsmart.zerocode.core.logbuilder.LogCorelationshipPrinter;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder.newInstance;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class ZeroCodeMultiStepsScenarioRunnerImpl implements ZeroCodeMultiStepsScenarioRunner {

    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeMultiStepsScenarioRunnerImpl.class);

    //guice -starts
    @Inject
    ObjectMapper objectMapper;

    @Inject
    ZeroCodeJsonTestProcesor zeroCodeJsonTestProcesor;

    @Inject
    private JsonServiceExecutor serviceExecutor;

    @Inject
    @Named("restful.application.endpoint.host")
    private String host;

    @Inject
    @Named("restful.application.endpoint.port")
    private String port;

    @Inject
    @Named("restful.application.endpoint.context")
    private String applicationContext;
    //guice -ends

    private LogCorelationshipPrinter logCorelationshipPrinter;

    private static StepNotificationHandler notificationHandler = new StepNotificationHandler();

    private ZeroCodeReportBuilder reportBuilder = ZeroCodeReportBuilder.newInstance().timeStamp(LocalDateTime.now());

    private ZeroCodeExecResultBuilder reportResultBuilder;

    private Boolean testPassed;

    @Override
    public boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

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
                    logCorelationshipPrinter = LogCorelationshipPrinter.newInstance(LOGGER);
                    logCorelationshipPrinter.stepLoop(i);

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

                    final String logPrefixRelationshipId = logCorelationshipPrinter.createRelationshipId();

                    try {
                        String serviceName = thisStep.getUrl();
                        String operationName = thisStep.getOperation();

                        // Resolve the URL patterns if any
                        serviceName = zeroCodeJsonTestProcesor.resolveStringJson(
                                serviceName,
                                scenarioExecutionState.getResolvedScenarioState()
                        );

                        // REST call execution
                        Boolean isRESTCall = false;
                        if (serviceName != null && serviceName.contains("/")) {
                            isRESTCall = true;
                        }
                        if (isRESTCall) {
                            serviceName = getFullyQualifiedRestUrl(serviceName);

                            // logging REST request
                            final LocalDateTime requestTimeStamp = LocalDateTime.now();
                            logCorelationshipPrinter.aRequestBuilder()
                                    .stepLoop(i)
                                    .relationshipId(logPrefixRelationshipId)
                                    .requestTimeStamp(requestTimeStamp)
                                    .step(thisStepName)
                                    .url(serviceName)
                                    .method(operationName)
                                    .request(prettyPrintJson(resolvedRequestJson));

                            executionResult = serviceExecutor.executeRESTService(serviceName, operationName, resolvedRequestJson);
                        } else {
                            // logging Java request
                            final LocalDateTime requestTimeStamp = LocalDateTime.now();
                            logCorelationshipPrinter.aRequestBuilder()
                                    .stepLoop(i)
                                    .relationshipId(logPrefixRelationshipId)
                                    .requestTimeStamp(requestTimeStamp)
                                    .step(thisStepName)
                                    .url(serviceName)
                                    .method(operationName)
                                    .request(prettyPrintJson(resolvedRequestJson));

                            executionResult = serviceExecutor.executeJavaService(serviceName, operationName, resolvedRequestJson);
                        }

                        // logging response
                        final LocalDateTime responseTimeStamp = LocalDateTime.now();
                        logCorelationshipPrinter.aResponseBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .responseTimeStamp(responseTimeStamp)
                                .response(executionResult);

                        stepExecutionState.addResponse(executionResult);
                        scenarioExecutionState.addStepState(stepExecutionState.getResolvedStep());

                        // Handle assertion section
                        String resolvedAssertionJson = zeroCodeJsonTestProcesor.resolveStringJson(
                                thisStep.getAssertions().toString(),
                                scenarioExecutionState.getResolvedScenarioState()
                        );

                        // logging assertion
                        logCorelationshipPrinter.assertion(prettyPrintJson(resolvedAssertionJson));

                        List<JsonAsserter> asserters = zeroCodeJsonTestProcesor.createAssertersFrom(resolvedAssertionJson);
                        List<AssertionReport> failureResults = zeroCodeJsonTestProcesor.assertAllAndReturnFailed(asserters, executionResult);

                        if (!failureResults.isEmpty()) {
                            /*
                             * Step failed
                             */
                            testPassed = notificationHandler.handleAssertion(
                                    notifier,
                                    description,
                                    scenario.getScenarioName(),
                                    thisStepName,
                                    failureResults,
                                    notificationHandler::handleAssertionFailed);

                            logCorelationshipPrinter.result(testPassed);

                            return testPassed;
                        }

                        /*
                         * Test step testPassed
                         */
                        testPassed = notificationHandler.handleAssertion(
                                notifier,
                                description,
                                scenario.getScenarioName(),
                                thisStepName,
                                failureResults,
                                notificationHandler::handleAssertionPassed);

                        logCorelationshipPrinter.result(testPassed);

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        // logging exception message
                        final LocalDateTime responseTimeStampEx = LocalDateTime.now();
                        logCorelationshipPrinter.aResponseBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .responseTimeStamp(responseTimeStampEx)
                                .response(executionResult)
                                .exceptionMessage(ex.getMessage());

                        /*
                         * Step threw an exception
                         */
                        testPassed = notificationHandler.handleAssertion(
                                notifier,
                                description,
                                scenario.getScenarioName(),
                                thisStepName,
                                (new RuntimeException("ZeroCode Step execution failed. Details:" + ex)),
                                notificationHandler::handleStepException);

                        logCorelationshipPrinter.result(testPassed);

                        return testPassed;

                    } finally {
                        logCorelationshipPrinter.print();

                        /*
                         * Build step report for each step
                         * Add the report step to the result step list.
                         */
                        reportResultBuilder.step(logCorelationshipPrinter.buildReportSingleStep());

                        /*
                         * failed and exception reports are generated here
                         */
                        if(!testPassed){
                            reportBuilder.result(reportResultBuilder.build());
                            reportBuilder.printToFile(scenario.getScenarioName() + ".json");
                        }
                    }

                } //<-- for each step-stepLoopTimes

            } //<--- steps for-each

            reportBuilder.result(reportResultBuilder.build());

        } //<-- Scenario Loop

        reportBuilder.printToFile(scenario.getScenarioName() + ".json");

        /*
         *  There were no steps to execute and the framework will display the test status as Green than Red.
         *  Red symbolises failure, but nothing has failed here.
         */
        return true;
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

    @Override
    public boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer testPassHandler) {

        scenarioSpec.getSteps()
                .forEach(step -> testPassHandler.accept(scenarioSpec.getScenarioName(), step.getName()));

        return true;
    }

}
