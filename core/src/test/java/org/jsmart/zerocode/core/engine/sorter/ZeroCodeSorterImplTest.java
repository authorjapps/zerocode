package org.jsmart.zerocode.core.engine.sorter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.jsonpath.Configuration;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.provider.JsonPathJacksonProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessorImpl;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ZeroCodeSorterImplTest {

    Injector injector;
    SmartUtils smartUtils;
    ObjectMapper mapper;

    ZeroCodeAssertionsProcessorImpl jsonPreProcessor;
    ZeroCodeSorterImpl sorter;


    @Before
    public void setUpStuff() throws Exception {
        String serverEnvFileName = "config_hosts_test.properties";
        injector = Guice.createInjector(new ApplicationMainModule(serverEnvFileName));
        smartUtils = injector.getInstance(SmartUtils.class);
        mapper = new ObjectMapperProvider().get();
        Configuration.setDefaults(new JsonPathJacksonProvider().get());
        jsonPreProcessor =
                new ZeroCodeAssertionsProcessorImpl(smartUtils.getMapper(), serverEnvFileName);

        sorter = new ZeroCodeSorterImpl(jsonPreProcessor, mapper);
    }


    @Test
    public void testSortResponseWithStringField() throws Exception {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/engine_unit_test_jsons/17_scenario_with_sort.json", ScenarioSpec.class);
        Step step = scenarioSpec.getSteps().get(0);

        String response = "{\n" +
                "  \"body\" : {\n" +
                "    \"persons\" : [" +
                "       {\"name\" : \"Ihor\"}," +
                "       {\"name\" : \"Andrew\"}" +
                "   ]\n" +
                "  }\n" +
                "}\n";

        String sortedResponse = "{\n" +
                "  \"body\" : {\n" +
                "    \"persons\" : [" +
                "       {\"name\" : \"Andrew\"}," +
                "       {\"name\" : \"Ihor\"}" +
                "   ]\n" +
                "  }\n" +
                "}\n";

        String result = sorter.sortArrayAndReplaceInResponse(step, response, scenarioExecutionState.getResolvedScenarioState());
        JSONAssert.assertEquals(sortedResponse, result, true);
    }

    @Test
    public void testSortResponseWithIntegerValueAndReverseOrder() throws Exception {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/engine_unit_test_jsons/17_scenario_with_sort.json", ScenarioSpec.class);
        Step step = scenarioSpec.getSteps().get(1);

        String response = "{\n" +
                "  \"body\" : {\n" +
                "    \"persons\" : [" +
                "       {\"id\" : 1}," +
                "       {\"id\" : 2}" +
                "   ]\n" +
                "  }\n" +
                "}\n";

        String sortedResponse = "{\n" +
                "  \"body\" : {\n" +
                "    \"persons\" : [" +
                "       {\"id\" : 2}," +
                "       {\"id\" : 1}" +
                "   ]\n" +
                "  }\n" +
                "}\n";

        String result = sorter.sortArrayAndReplaceInResponse(step, response, scenarioExecutionState.getResolvedScenarioState());
        JSONAssert.assertEquals(sortedResponse, result, true);
    }

    @Test
    public void testSortResponseWithDefaultOrder() throws Exception {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/engine_unit_test_jsons/17_scenario_with_sort.json", ScenarioSpec.class);
        Step step = scenarioSpec.getSteps().get(2);

        String response = "{\n" +
                "  \"body\" : {\n" +
                "    \"persons\" : [" +
                "       {\"id\" : 2}," +
                "       {\"id\" : 1}" +
                "   ]\n" +
                "  }\n" +
                "}\n";

        String sortedResponse = "{\n" +
                "  \"body\" : {\n" +
                "    \"persons\" : [" +
                "       {\"id\" : 1}," +
                "       {\"id\" : 2}" +
                "   ]\n" +
                "  }\n" +
                "}\n";

        String result = sorter.sortArrayAndReplaceInResponse(step, response, scenarioExecutionState.getResolvedScenarioState());
        JSONAssert.assertEquals(sortedResponse, result, true);
    }

}
