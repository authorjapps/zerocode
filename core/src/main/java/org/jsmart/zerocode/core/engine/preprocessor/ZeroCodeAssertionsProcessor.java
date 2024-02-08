package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import java.util.List;

public interface ZeroCodeAssertionsProcessor {

    String resolveStringJson(String requestJsonAsString, String resolvedScenarioState);

    String resolveKnownTokensAndProperties(String requestJsonOrAnyString);

    String resolveJsonPaths(String resolvedFromTemplate, String jsonString);

    List<String> getAllJsonPathTokens(String requestJsonAsString);

    List<JsonAsserter> createJsonAsserters(String resolvedAssertionJson);

    List<FieldAssertionMatcher> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult);

    Step resolveJsonContent(Step thisStep, ScenarioExecutionState scenarioExecutionState);

    String fieldMasksRemoved(String resolvedRequestJson);

    String fieldMasksApplied(String resolvedRequestJson);

}
