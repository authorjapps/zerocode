package org.jsmart.zerocode.core.engine.preprocessor;

import java.util.List;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

public interface ZeroCodeJsonTestProcesor {

  String resolveStringJson(String requestJsonAsString, String resolvedScenarioState);

  String resolveKnownTokensAndProperties(String requestJsonOrAnyString);

  String resolveJsonPaths(String resolvedFromTemplate, String jsonString);

  List<String> getAllJsonPathTokens(String requestJsonAsString);

  List<JsonAsserter> createJsonAsserters(String resolvedAssertionJson);

  List<FieldAssertionMatcher> assertAllAndReturnFailed(
      List<JsonAsserter> asserters, String executionResult);
}
