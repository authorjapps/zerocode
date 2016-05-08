package org.jsmart.smarttester.core.runner;

import com.google.inject.Singleton;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.domain.Request;
import org.jsmart.smarttester.core.domain.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Singleton
public class DefaultSmartMultiStepsRunner implements MultiStepsRunner {
    @Override
    public boolean runSteps(FlowSpec flowSpec, FlowStepStatusNotifier flowStepStatusNotifier) {

        for(Step thisStep : flowSpec.getSteps()){

            final Request restRequest = thisStep.getRequest();
            System.out.println( "### RAW: restRequest : " + restRequest);

            // TODO: Resolve the place holders at this point including .SIZE etc

            // TODO: Execute the step

            try{
                // TODO: write executing code here ie fore the rest call etc
                //write code here

                // TODO: Collect the assertion result into this list, say field by field
                List<JsonAssertionFailureResult> failureResults = new ArrayList<>();

                // TODO: During this step: if assertion failed
                if (!failureResults.isEmpty()) {
                    return flowStepStatusNotifier.notifyFlowStepAssertionFailed(flowSpec.getFlowName(), thisStep.getName(), failureResults);
                }

                // TODO: Otherwise test passed
                return flowStepStatusNotifier.notifyFlowStepExecutionPassed(flowSpec.getFlowName(), thisStep.getName());

            } catch(Exception ex){
                // During this step: if any exception occurred
                return flowStepStatusNotifier.notifyFlowStepExecutionException(
                        flowSpec.getFlowName(),
                        thisStep.getName(),
                        new RuntimeException("Step execution failed")
                );
            }

        }

        /*
         *  There were no steps to execute and the framework will display test Green than Red.
         */
        return true;
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
