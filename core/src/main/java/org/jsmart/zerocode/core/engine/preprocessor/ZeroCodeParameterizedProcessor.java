package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;

public interface ZeroCodeParameterizedProcessor {

    ScenarioSpec resolveParameterized(ScenarioSpec scenario, int iteration);

    Step resolveStepParameters(ScenarioSpec scenario, int iteration, Step step);
}
