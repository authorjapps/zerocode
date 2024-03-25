package org.jsmart.zerocode.core.engine.validators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.jsonpath.Configuration;
import org.jsmart.zerocode.TestUtility;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.provider.JsonPathJacksonProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.StepExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessorImpl;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ZeroCodeValidatorImplTest {
    Injector injector;
    SmartUtils smartUtils;
    ObjectMapper mapper;

    ZeroCodeAssertionsProcessorImpl jsonPreProcessor;

    ZeroCodeValidatorImpl codeValidator;

    @Before
    public void setUpStuff() throws Exception {
        String serverEnvFileName = "config_hosts_test.properties";
        injector = Guice.createInjector(new ApplicationMainModule(serverEnvFileName));
        smartUtils = injector.getInstance(SmartUtils.class);
        mapper = new ObjectMapperProvider().get();
        Configuration.setDefaults(new JsonPathJacksonProvider().get());


        jsonPreProcessor =
                new ZeroCodeAssertionsProcessorImpl(smartUtils.getMapper(), serverEnvFileName);

        codeValidator = new ZeroCodeValidatorImpl(jsonPreProcessor);
    }

    @Test
    public void test_validateFlat_happy() throws Exception {

        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/engine_unit_test_jsons/15_test_validators_single_step.json", ScenarioSpec.class);
        Step step = scenarioSpec.getSteps().get(0);

        String actualResult = "{\n" +
                "                \"status\": 200,\n" +
                "                \"body\": {\n" +
                "                    \"name\": \"Mr Bean\"\n" +
                "                }\n" +
                "            }";

        List<FieldAssertionMatcher> matchers = codeValidator.validateFlat(step, actualResult, "resolvedScenarioState");
        assertThat(matchers.size(), is(0));

    }

    @Test
    public void test_validateFlat_supportJsonPathExpressions() throws Exception {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();
        scenarioExecutionState.addStepState(createResolvedScenarioState());
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/engine_unit_test_jsons/16_test_validators_jsonpath_expressions_support.json", ScenarioSpec.class);
        Step step = scenarioSpec.getSteps().get(1);

        String actualResult = "{\n" +
                "  \"records\" : [ {\n" +
                "    \"key\" : null,\n" +
                "    \"value\" : {\n" +
                "      \"test\" : \"1\"\n" +
                "    },\n" +
                "    \"headers\" : {\n" +
                "      \"CORRELATION_ID\" : \"test\"\n" + // THIS value allow to match with the jsonpath expression
                "    }\n" +
                "  } ],\n" +
                "  \"size\" : 1\n" +
                "}";

        List<FieldAssertionMatcher> matchers = codeValidator.validateFlat(step, actualResult, scenarioExecutionState.getResolvedScenarioState());
        assertThat(matchers.size(), is(0));

    }

    private StepExecutionState createResolvedScenarioState() {
        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(TestUtility.createDummyStep("produce_step"));
        stepExecutionState.addRequest("{\n" +
                "\"recordType\":\"JSON\"," +
                "\"records\":[{\"key\":null,\"headers\":{\"CORRELATION_ID\":\"test\"},\"value\":{\"test\":\"1\"}}]\n" +
                "}");
        stepExecutionState.addResponse("{\n" +
                "    \"id\" : 10101\n" +
                "}");

        return stepExecutionState;
    }

    @Test
    public void test_validateFlat_nonMatching() throws Exception {

        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/engine_unit_test_jsons/15_test_validators_single_step.json", ScenarioSpec.class);
        Step step = scenarioSpec.getSteps().get(0);

        String actualResult = "{\n" +
                "                \"status\": 201,\n" +
                "                \"body\": {\n" +
                "                    \"name\": \"Mrs X\"\n" +
                "                }\n" +
                "            }";

        List<FieldAssertionMatcher> matchers = codeValidator.validateFlat(step, actualResult, "resolvedScenarioState");
        assertThat(matchers.size(), is(2));
        assertThat(matchers.get(0).toString(), containsString("actual value 'Mrs X' did not match the expected value 'Mr Bean'"));
        assertThat(matchers.get(1).toString(), containsString("actual value '201' did not match the expected value '200'"));

        //TODO [TECH-DEBT - Bulk or section validator]
        //assertThat(matchers.get(0).toString(), containsString("'$.body.name' with actual value"));
        //assertThat(matchers.get(1).toString(), containsString("'$.status' with actual value "));
    }
}
