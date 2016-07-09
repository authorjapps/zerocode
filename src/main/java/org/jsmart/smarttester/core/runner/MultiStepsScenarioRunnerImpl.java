package org.jsmart.smarttester.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import org.jsmart.smarttester.core.domain.ScenarioSpec;
import org.jsmart.smarttester.core.domain.Step;
import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.jsmart.smarttester.core.engine.assertion.JsonAsserter;
import org.jsmart.smarttester.core.engine.executor.JsonServiceExecutor;
import org.jsmart.smarttester.core.engine.preprocessor.JsonTestProcesor;
import org.jsmart.smarttester.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.smarttester.core.engine.preprocessor.StepExecutionState;
import org.jsmart.smarttester.core.logbuilder.LogCorelationshipPrinter;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.jsmart.smarttester.core.logbuilder.LogCorelationshipPrinter.createRelationshipId;
import static org.jsmart.smarttester.core.logbuilder.LogCorelationshipPrinter.newInstance;
import static org.jsmart.smarttester.core.utils.SmartUtils.prettyPrintJson;

@Singleton
public class MultiStepsScenarioRunnerImpl implements MultiStepsScenarioRunner {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MultiStepsScenarioRunnerImpl.class);

    //guice -starts
    @Inject
    ObjectMapper objectMapper;

    @Inject
    JsonTestProcesor jsonTestProcesor;

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

    private static StepNotificationHandler notificationHandler = new StepNotificationHandler();

    @Override
    public boolean runScenario(ScenarioSpec scenario, RunNotifier notifier, Description description) {

        LOGGER.info("\n-------------------------- Scenario:{} -------------------------\n", scenario.getScenarioName());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        for(Step thisStep : scenario.getSteps()){
            // Another way to get the String
            // String requestJson = objectMapper.valueToTree(thisStep.getRequest()).toString();

            LogCorelationshipPrinter logCorelationshipPrinter = newInstance(LOGGER);

            final String requestJsonAsString = thisStep.getRequest().toString();

            StepExecutionState stepExecutionState = new StepExecutionState();
            stepExecutionState.addStep(thisStep.getName());

            String resolvedRequestJson = jsonTestProcesor.resolveStringJson(
                    requestJsonAsString,
                    scenarioExecutionState.getResolvedScenarioState()
            );
            stepExecutionState.addRequest(resolvedRequestJson);

            String executionResult = "-response not decided-";
            final String logPrefixRelationshipId = createRelationshipId();
            try{
                String serviceName = thisStep.getUrl();
                String operationName = thisStep.getOperation();

                // Resolve the URL patterns if any
                serviceName = jsonTestProcesor.resolveStringJson(
                        serviceName,
                        scenarioExecutionState.getResolvedScenarioState()
                );

                // REST call execution
                Boolean isRESTCall = false;
                if( serviceName != null && serviceName.contains("/")) {
                    isRESTCall = true;
                }
                if(isRESTCall) {
                    serviceName = getFullyQualifiedRestUrl(serviceName);

                    // logging REST request
                    logCorelationshipPrinter.aRequestBuilder()
                            .relationshipId(logPrefixRelationshipId)
                            .requestTimeStamp(LocalDateTime.now())
                            .step(thisStep.getName())
                            .url(serviceName)
                            .method(operationName)
                            .request(SmartUtils.prettyPrintJson(resolvedRequestJson));

                    executionResult = serviceExecutor.executeRESTService(serviceName, operationName, resolvedRequestJson);
                }
                else {
                    // logging Java request
                    logCorelationshipPrinter.aRequestBuilder()
                            .relationshipId(logPrefixRelationshipId)
                            .requestTimeStamp(LocalDateTime.now())
                            .step(thisStep.getName())
                            .url(serviceName)
                            .method(operationName)
                            .request(SmartUtils.prettyPrintJson(resolvedRequestJson));

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
                String resolvedAssertionJson = jsonTestProcesor.resolveStringJson(
                        thisStep.getAssertions().toString(),
                        scenarioExecutionState.getResolvedScenarioState()
                );

                // logging assertion
                logCorelationshipPrinter.assertion(prettyPrintJson(resolvedAssertionJson));

                List<JsonAsserter> asserters = jsonTestProcesor.createAssertersFrom(resolvedAssertionJson);
                List<AssertionReport> failureResults = jsonTestProcesor.assertAllAndReturnFailed(asserters, executionResult); //<-- write code

                if (!failureResults.isEmpty()) {
                    /*
                     * Step failed
                     */
                    return notificationHandler.handleAssertion(
                            notifier,
                            description,
                            scenario.getScenarioName(),
                            thisStep.getName(),
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
                        thisStep.getName(),
                        failureResults,
                        notificationHandler::handleAssertionPassed);

            } catch(Exception ex){
                ex.printStackTrace();
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
                        thisStep.getName(),
                        (new RuntimeException("ZeroCode Step execution failed. Details:" + ex)),
                        notificationHandler::handleStepException);

            }
            finally {
                logCorelationshipPrinter.print();
            }
        }

        /*
         *  There were no steps to execute and the framework will display the test status as Green than Red.
         *  Red symbolises failure, but nothing has failed here.
         */
        return true;
    }

    private String getFullyQualifiedRestUrl(String serviceEndPoint) {
        if(serviceEndPoint.startsWith("http://") || serviceEndPoint.startsWith("https://")) {
            return serviceEndPoint;
        } else {
            /*
             * Make sure your property file contains contextpath with a front slash like "/google-map".
             * -OR-
             * Empty context path is also ok if it requires. In this case dont put front slash.
             */
            return String.format("%s:%s%s%s",host, port, applicationContext, serviceEndPoint );
        }
    }

    @Override
    public boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer testPassHandler) {

        scenarioSpec.getSteps()
                .forEach(step -> testPassHandler.accept(scenarioSpec.getScenarioName(), step.getName()));

        return true;
    }

}
