package org.jsmart.zerocode.core.engine.validators;

import java.util.List;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

public interface ZeroCodeValidator {

    List<FieldAssertionMatcher> validateFlat(Step thisStep, String actualResult, String resolvedScenarioState);

    List<FieldAssertionMatcher> validateStrict(String expectedResult, String actualResult);

    List<FieldAssertionMatcher> validateLenient(String expectedResult, String actualResult);

}
