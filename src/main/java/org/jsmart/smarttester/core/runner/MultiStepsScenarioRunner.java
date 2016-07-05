package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.domain.ScenarioSpec;

import java.util.function.BiConsumer;

public interface MultiStepsScenarioRunner {

    boolean runScenario(ScenarioSpec scenarioSpec, FlowStepStatusNotifier flowStepStatusNotifier);

    boolean runChildStep(ScenarioSpec scenarioSpec,
                                 BiConsumer<String, String> testPassHandler
                                 );

}
