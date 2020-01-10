package org.jsmart.zerocode.core.engine.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.Validator;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasExactValueAsserter;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessor;
import org.slf4j.Logger;

import static org.jsmart.zerocode.core.utils.HelperJsonUtils.strictComparePayload;
import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeValidatorImpl implements ZeroCodeValidator {
    private static final Logger LOGGER = getLogger(ZeroCodeValidatorImpl.class);

    private final ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor;

    @Inject
    public ZeroCodeValidatorImpl(ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor) {
        this.zeroCodeAssertionsProcessor = zeroCodeAssertionsProcessor;
    }

    @Override
    public List<FieldAssertionMatcher> validateFlat(Step thisStep, String actualResult) {
        LOGGER.info("Comparing results via flat validators");

        List<FieldAssertionMatcher> failureResults = new ArrayList<>();
        List<Validator> validators = thisStep.getValidators();

        for (Validator validator : validators) {
            String josnPath = validator.getField();
            JsonNode expectedValue = validator.getValue();
            Object actualValue = JsonPath.read(actualResult, josnPath);

            List<JsonAsserter> asserters = zeroCodeAssertionsProcessor.createJsonAsserters(expectedValue.toString());

            failureResults.addAll(zeroCodeAssertionsProcessor.assertAllAndReturnFailed(asserters, actualValue.toString()));
        }

        return failureResults;
    }

    @Override
    public List<FieldAssertionMatcher> validateStrict(String expectedResult, String actualResult) {
        LOGGER.info("Comparing results via STRICT matchers");

        return strictComparePayload(expectedResult, actualResult);
    }

    @Override
    public List<FieldAssertionMatcher> validateLenient(String expectedResult, String actualResult) {
        LOGGER.info("Comparing results via LENIENT matchers");

        List<JsonAsserter> asserters = zeroCodeAssertionsProcessor.createJsonAsserters(expectedResult);
        return zeroCodeAssertionsProcessor.assertAllAndReturnFailed(asserters, actualResult);
    }
}
