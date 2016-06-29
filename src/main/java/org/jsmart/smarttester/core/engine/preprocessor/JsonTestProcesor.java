package org.jsmart.smarttester.core.engine.preprocessor;

import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.jsmart.smarttester.core.engine.assertion.JsonAsserter;

import java.util.List;

public interface JsonTestProcesor {

    String resolveRequestJson(String requestJsonAsString, String resolvedScenarioState);

    List<String> getAllTokens(String requestJsonAsString);

    String resolveJsonPaths(String resolvedFromTemplate, String jsonString);

    public List<String> getAllJsonPathTokens(String requestJsonAsString);

    List<JsonAsserter> createAssertersFrom(String resolvedAssertionJson);

    List<AssertionReport> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult);
}
