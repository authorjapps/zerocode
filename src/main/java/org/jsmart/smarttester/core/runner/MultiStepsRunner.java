package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.domain.FlowSpec;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface MultiStepsRunner {

    boolean runSteps(FlowSpec flowSpec, FlowRunningObserver observer);
    boolean runChildStep(FlowSpec flowSpec,
                                 BiConsumer<String, String> testPassHandler
                                 );
}
