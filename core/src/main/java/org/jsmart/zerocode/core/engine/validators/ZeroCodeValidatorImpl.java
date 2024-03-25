package org.jsmart.zerocode.core.engine.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.jsmart.zerocode.core.di.provider.JsonPathJacksonProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.domain.Validator;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.jsmart.zerocode.core.utils.HelperJsonUtils.strictComparePayload;
import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeValidatorImpl implements ZeroCodeValidator {
    private static final Logger LOGGER = getLogger(ZeroCodeValidatorImpl.class);

    private final ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor;

    private final ObjectMapper mapper;

    @Inject
    public ZeroCodeValidatorImpl(ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor) {
        this.zeroCodeAssertionsProcessor = zeroCodeAssertionsProcessor;
        this.mapper = new ObjectMapperProvider().get();
        Configuration.setDefaults(new JsonPathJacksonProvider().get());
    }

    @Override
    public List<FieldAssertionMatcher> validateFlat(Step thisStep, String actualResult, String resolvedScenarioState) {
        LOGGER.debug("Comparing results via flat validators");

        List<FieldAssertionMatcher> failureResults = new ArrayList<>();
        List<Validator> validators = thisStep.getValidators();

        for (Validator validator : validators) {
            String jsonPath = validator.getField();

            String transformed = zeroCodeAssertionsProcessor.resolveStringJson(jsonPath, resolvedScenarioState);

            JsonNode expectedValue = validator.getValue();

            Object actualValue = JsonPath.read(actualResult, transformed);
            String actualString;
            try {
                actualString = this.mapper.writeValueAsString(actualValue);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            List<JsonAsserter> asserters = zeroCodeAssertionsProcessor.createJsonAsserters(expectedValue.toString());

            failureResults.addAll(zeroCodeAssertionsProcessor.assertAllAndReturnFailed(asserters, actualString));
        }

        return failureResults;
    }

    @Override
    public List<FieldAssertionMatcher> validateStrict(String expectedResult, String actualResult) {
        LOGGER.debug("Comparing results via STRICT matchers");

        return strictComparePayload(expectedResult, actualResult);
    }

    @Override
    public List<FieldAssertionMatcher> validateLenient(String expectedResult, String actualResult) {
        LOGGER.debug("Comparing results via LENIENT matchers");

        List<JsonAsserter> asserters = zeroCodeAssertionsProcessor.createJsonAsserters(expectedResult);
        return zeroCodeAssertionsProcessor.assertAllAndReturnFailed(asserters, actualResult);
    }
}
