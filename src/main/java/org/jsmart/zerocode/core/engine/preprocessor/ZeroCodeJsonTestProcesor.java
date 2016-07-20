package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.core.engine.assertion.AssertionReport;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import java.util.List;

public interface ZeroCodeJsonTestProcesor {

    String resolveStringJson(String requestJsonAsString, String resolvedScenarioState);

    List<String> getAllTokens(String requestJsonAsString);

    String resolveJsonPaths(String resolvedFromTemplate, String jsonString);

    public List<String> getAllJsonPathTokens(String requestJsonAsString);

    List<JsonAsserter> createAssertersFrom(String resolvedAssertionJson);

    List<AssertionReport> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult);
}
