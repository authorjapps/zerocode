package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.core.domain.Step;

import java.util.List;

public interface ZeroCodeExternalFileProcessor {

    Step resolveExtJsonFile(Step thisStep);

    List<Step> createFromStepFile(Step thisStep, String stepId);
}
