package org.jsmart.zerocode.core.runner;

import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.function.BiConsumer;

public interface ZeroCodeMultiStepsScenarioRunner {

    boolean runScenario(ScenarioSpec scenarioSpec, RunNotifier notifier, Description description);

    boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer<String, String> testPassHandler);

}
