package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.core.domain.Step;

import java.util.List;

public interface ZeroCodeExternalFileProcessor {

    Step resolveExtJsonFile(Step thisStep);

    /**
     * Replaces the ${PARAM.name} tokens of an inlined stepFile/stepFiles with the step's "parameters".
     *
     * @param rawStep     the step as written in the scenario, before the external file was inlined
     * @param inlinedStep the step after {@link #resolveExtJsonFile(Step)}
     * @return the step with its step file parameters resolved
     */
    Step resolveStepFileParameters(Step rawStep, Step inlinedStep);

    List<Step> createFromStepFile(Step thisStep, String stepId);
}
