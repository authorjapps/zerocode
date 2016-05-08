package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.domain.FlowSpec;

import java.util.function.BiConsumer;

public interface MultiStepsRunner {

    boolean runSteps(FlowSpec flowSpec, FlowStepStatusNotifier flowStepStatusNotifier);

    boolean runChildStep(FlowSpec flowSpec,
                                 BiConsumer<String, String> testPassHandler
                                 );

}
