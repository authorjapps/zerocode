package org.jsmart.zerocode.core.engine.sorter;

import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

import java.util.List;

public interface ZeroCodeSorter {

    String sortArrayAndReplaceInResponse(Step thisStep, String results, String resolvedScenarioState);
}
