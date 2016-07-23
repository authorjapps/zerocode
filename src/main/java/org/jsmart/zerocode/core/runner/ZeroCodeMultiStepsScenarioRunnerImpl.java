package org.jsmart.zerocode.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeReportStepBuilder;
import org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeResultBuilder;
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

import static org.jsmart.zerocode.core.domain.reports.builders.ZeroCodeResultBuilder.newInstance;
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

    @Override
    public boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

        LOGGER.info("\n-------------------------- BDD: Scenario:{} -------------------------\n", scenario.getScenarioName());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        final int scenarioLoopCount = scenario.getLoop() == null ? 1 : scenario.getLoop();


        for (int k = 0; k < scenarioLoopCount; k++) {

            LOGGER.info("\n### Executing Scenario -->> Count No: " + k);

            /*
             * Build Report scenario for each k
             */
            ZeroCodeResultBuilder reportResultBuilder = newInstance()
                    .loop(k > 0 ? (k) : null)
                    .scenarioName(scenario.getScenarioName());

            for (Step thisStep : scenario.getSteps()) {
                // Another way to get the String
                // String requestJson = objectMapper.valueToTree(thisStep.getRequest()).toString();

                final int loop = thisStep.getLoop() == null ? 1 : thisStep.getLoop();
                for (int i = 0; i < loop; i++) {
                    LOGGER.info("\n### Executing Step -->> Count No: " + i);
                    logCorelationshipPrinter = LogCorelationshipPrinter.newInstance(LOGGER);

                    /*
                     * Build step report for each step
                     */
                    ZeroCodeReportStepBuilder reportStepBuilder = ZeroCodeReportStepBuilder.newInstance();
                    reportStepBuilder.loop(i > 0 ? (i) : null);


                    final String requestJsonAsString = thisStep.getRequest().toString();

                    StepExecutionState stepExecutionState = new StepExecutionState();

                    /*
                     * Create the step name as it is for the 1st loop ie i=0.
                     * Rest of the loops suffix as i ie stepName1, stepName2, stepName3 etc
                     */
                    final String thisStepName = thisStep.getName() + (i == 0 ? "" : i);

                    reportStepBuilder.name(thisStepName);

                    stepExecutionState.addStep(thisStepName);

                    String resolvedRequestJson = zeroCodeJsonTestProcesor.resolveStringJson(
                            requestJsonAsString,
                            scenarioExecutionState.getResolvedScenarioState()
                    );
                    stepExecutionState.addRequest(resolvedRequestJson);

                    String executionResult = "-response not decided-";
                    final String logPrefixRelationshipId = LogCorelationshipPrinter.createRelationshipId();
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
                            logCorelationshipPrinter.aRequestBuilder()
                                    .relationshipId(logPrefixRelationshipId)
                                    .requestTimeStamp(LocalDateTime.now())
                                    .step(thisStepName)
                                    .url(serviceName)
                                    .method(operationName)
                                    .request(prettyPrintJson(resolvedRequestJson));

                            executionResult = serviceExecutor.executeRESTService(serviceName, operationName, resolvedRequestJson);
                        } else {
                            // logging Java request
                            logCorelationshipPrinter.aRequestBuilder()
                                    .relationshipId(logPrefixRelationshipId)
                                    .requestTimeStamp(LocalDateTime.now())
                                    .step(thisStepName)
                                    .url(serviceName)
                                    .method(operationName)
                                    .request(prettyPrintJson(resolvedRequestJson));

                            executionResult = serviceExecutor.executeJavaService(serviceName, operationName, resolvedRequestJson);
                        }

                        // logging response
                        logCorelationshipPrinter.aResponseBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .responseTimeStamp(LocalDateTime.now())
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
                            return notificationHandler.handleAssertion(
                                    notifier,
                                    description,
                                    scenario.getScenarioName(),
                                    thisStepName,
                                    failureResults,
                                    notificationHandler::handleAssertionFailed);
                        }

                        /*
                         * Test step passed
                         */
                        notificationHandler.handleAssertion(
                                notifier,
                                description,
                                scenario.getScenarioName(),
                                thisStepName,
                                failureResults,
                                notificationHandler::handleAssertionPassed);

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        // logging exception message
                        logCorelationshipPrinter.aResponseBuilder()
                                .relationshipId(logPrefixRelationshipId)
                                .responseTimeStamp(LocalDateTime.now())
                                .response(executionResult)
                                .exceptionMessage(ex.getMessage());

                        /*
                         * Step threw an exception
                         */
                        return notificationHandler.handleAssertion(
                                notifier,
                                description,
                                scenario.getScenarioName(),
                                thisStepName,
                                (new RuntimeException("ZeroCode Step execution failed. Details:" + ex)),
                                notificationHandler::handleStepException);

                    } finally {
                        logCorelationshipPrinter.print();
                    }

                    /*
                     * Add the report step to the result step list.
                     */
                    reportResultBuilder.step(reportStepBuilder.build());

                } //<-- loop for each step
            } //<--- steps for loop

            reportBuilder.result(reportResultBuilder.build());

        } //<-- Scenario Loop

        final ZeroCodeReport scenarioReport = reportBuilder.build();

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
