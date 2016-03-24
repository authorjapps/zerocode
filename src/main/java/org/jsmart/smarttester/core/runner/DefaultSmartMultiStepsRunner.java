package org.jsmart.smarttester.core.runner;

import com.google.inject.Singleton;
import org.jsmart.smarttester.core.domain.FlowSpec;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Singleton
public class DefaultSmartMultiStepsRunner implements MultiStepsRunner {
    @Override
    public boolean runSteps(FlowSpec flowSpec, FlowRunningObserver flowRunningObserver) {

        flowRunningObserver.testRanSuccessFully();

        return true;
    }

    @Override
    public boolean runChildStep(FlowSpec flowSpec, BiConsumer testPassHandler) {

        flowSpec.getSteps()
                .forEach(step -> testPassHandler.accept(flowSpec.getFlowName(), step.getName()));

        return true;
    }

    /*@Override
    public boolean runChildStepWithObserver(FlowSpec flowSpec, BiConsumer<FlowRunningObserver, String> testObserver) {

        flowSpec.getSteps()
                .forEach(step -> {
                    testObserver.acc
                });

                //.forEach(step -> testPassHandler.accept(flowSpec.getFlowName(), step.getName()));

        return true;
    }*/
}
