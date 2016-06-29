package org.jsmart.smarttester.core.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.domain.Step;
import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.jsmart.smarttester.core.engine.assertion.JsonAsserter;
import org.jsmart.smarttester.core.engine.executor.JsonServiceExecutor;
import org.jsmart.smarttester.core.engine.preprocessor.JsonTestProcesor;
import org.jsmart.smarttester.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.smarttester.core.engine.preprocessor.StepExecutionState;
import org.jsmart.smarttester.core.logbuilder.LogCorelationshipPrinter;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.lang.String.format;
import static org.jsmart.smarttester.core.logbuilder.LogCorelationshipPrinter.createRelationshipId;
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

    LogCorelationshipPrinter logCorelationshipPrinter = LogCorelationshipPrinter.newInstance(LOGGER);

    @Override
    public boolean runSteps(FlowSpec scenario, FlowStepStatusNotifier flowStepStatusNotifier) {

        LOGGER.info("\n-------------------------- Scenario:{} -------------------------\n", scenario.getFlowName());

        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        for(Step thisStep : scenario.getSteps()){
            // Another way to get the String
            // String requestJson = objectMapper.valueToTree(thisStep.getRequest()).toString();

            final String requestJsonAsString = thisStep.getRequest().toString();

            StepExecutionState stepExecutionState = new StepExecutionState();
            stepExecutionState.addStep(thisStep.getName());

            String resolvedRequestJson = jsonTestProcesor.resolveStringJson(
                    requestJsonAsString,
                    scenarioExecutionState.getResolvedScenarioState()
            );
            stepExecutionState.addRequest(resolvedRequestJson);

            String executionResult;
            final String logPrefixRelationshipId = createRelationshipId();
            try{
                String serviceName = thisStep.getUrl();
                String operationName = thisStep.getOperation();

                // Resolve the URL patterns if any
                serviceName = jsonTestProcesor.resolveStringJson(
                        serviceName,
                        scenarioExecutionState.getResolvedScenarioState()
                );

                //
                logCorelationshipPrinter.aRequestBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .requestTimeStamp(LocalDateTime.now())
                        .step(thisStep.getName())
                        .url(serviceName)
                        .method(operationName)
                        .request(SmartUtils.prettyPrintJson(resolvedRequestJson));
                //

                // REST call execution
                Boolean isRESTCall = false;
                if( serviceName != null && serviceName.contains("/")) {
                    isRESTCall = true;
                }
                if(isRESTCall) {
                    serviceName = getFullyQualifiedRestUrl(serviceName);
                    executionResult = serviceExecutor.executeRESTService(serviceName, operationName, resolvedRequestJson);
                }
                else {
                    executionResult = serviceExecutor.executeJavaService(serviceName, operationName, resolvedRequestJson);
                }

                //
                logCorelationshipPrinter.aResponseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(LocalDateTime.now())
                        .response(executionResult);
                //

                stepExecutionState.addResponse(executionResult);
                scenarioExecutionState.addStepState(stepExecutionState.getResolvedStep());

                // Handle assertion section
                String resolvedAssertionJson = jsonTestProcesor.resolveStringJson(
                        thisStep.getAssertions().toString(),
                        scenarioExecutionState.getResolvedScenarioState()
                );

                LOGGER.info("\n---------> Assertion: <----------\n{}", prettyPrintJson(resolvedAssertionJson));

                List<JsonAsserter> asserters = jsonTestProcesor.createAssertersFrom(resolvedAssertionJson);
                List<AssertionReport> failureResults = jsonTestProcesor.assertAllAndReturnFailed(asserters, executionResult); //<-- write code

                // TODO: During this step: if assertion failed
                if (!failureResults.isEmpty()) {
                    return flowStepStatusNotifier.notifyFlowStepAssertionFailed(scenario.getFlowName(), thisStep.getName(), failureResults);
                }

                // TODO: Otherwise test passed
                flowStepStatusNotifier.notifyFlowStepExecutionPassed(scenario.getFlowName(), thisStep.getName());

            } catch(Exception ex){
                logCorelationshipPrinter.aResponseBuilder()
                        .relationshipId(logPrefixRelationshipId)
                        .responseTimeStamp(LocalDateTime.now())
                        .response(ex.getMessage());

                // During this step: if any exception occurred
                return flowStepStatusNotifier.notifyFlowStepExecutionException(
                        scenario.getFlowName(),
                        thisStep.getName(),
                        new RuntimeException("Smart Step execution failed. Details:" + ex)
                );
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
            applicationContext = StringUtils.isEmpty(applicationContext) ? "" : "/" + applicationContext;
            return String.format("%s:%s%s%s",host, port, applicationContext, serviceEndPoint );
        }
    }

    @Override
    public boolean runChildStep(FlowSpec flowSpec, BiConsumer testPassHandler) {

        flowSpec.getSteps()
                .forEach(step -> testPassHandler.accept(flowSpec.getFlowName(), step.getName()));

        return true;
    }


    /*@Override
    public boolean runChildStepWithObserver(FlowSpec flowSpec, BiConsumer<FlowStepStatusNotifier, String> testObserver) {

        flowSpec.getSteps()
                .forEach(step -> {
                    testObserver.acc
                });

                //.forEach(step -> testPassHandler.accept(flowSpec.getFlowName(), step.getName()));

        return true;
    }*/

}
