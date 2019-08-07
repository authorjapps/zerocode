package org.jsmart.zerocode.core.runner;

import java.util.function.BiConsumer;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

public interface ZeroCodeMultiStepsScenarioRunner {

  boolean runScenario(ScenarioSpec scenarioSpec, RunNotifier notifier, Description description);

  boolean runChildStep(ScenarioSpec scenarioSpec, BiConsumer<String, String> testPassHandler);
}
