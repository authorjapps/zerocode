package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(JukitoRunner.class)
//@UseModules(ApplicationMainModule.class)
public class ScenarioSpecTest {
    public static class JukitoModule extends TestModule {
        @Override
        protected void configureTest() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test.properties");

            /* Finally install the main module */
            install(applicationMainModule);
        }
    }

    @Inject
    SmartUtils smartUtils;

    @Inject
    private ObjectMapper mapper;

    @Before
    public void beforeMethod() throws Exception {
        // No need of this as the mapper has been set to all these.
        //mapper = new ObjectMapper();
        //mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    //@Description("JukitoDescription")
    @Test
    public void willDeserializeA_VanilaFlow() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/02_test_json_flow_single_step.json");
        ScenarioSpec scenarioDeserialized = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        assertThat(scenarioDeserialized, notNullValue());
        assertThat(scenarioDeserialized.getSteps().size(), is(1));
        assertThat(scenarioDeserialized.getLoop(), is(5));
        assertThat(scenarioDeserialized.getScenarioName(), containsString("Given_When_Then-Flow"));
    }

    @Test
    public void willDeserializeA_MultiSteps() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/03_test_json_flow_multi_step.json");
        ScenarioSpec scenarioDeserialized = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        assertThat(scenarioDeserialized, notNullValue());
        assertThat(scenarioDeserialized.getSteps().size(), is(2));
        assertThat(scenarioDeserialized.getScenarioName(), containsString("Given_When_Then-Flow"));
        assertThat(scenarioDeserialized.getSteps().get(1).getUrl(), containsString("/url2/path"));
    }

    @Test
    public void shouldSerializeSingleFlow() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/03_test_json_flow_multi_step.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        JsonNode scenarioSpecNode = mapper.valueToTree(scenarioSpec);

        /**
         * Note:
         * jayway json assertEquals has issues if json doc has got comments. So find out how to ignore or allow comments
         */
        JSONAssert.assertEquals(jsonDocumentAsString, scenarioSpecNode.toString(), false);

        assertThat(scenarioSpecNode.get("scenarioName").asText(), containsString("Given_When_Then"));
        assertThat(scenarioSpecNode.get("loop").asInt(), is(5));
    }


    @Test
    public void testJSOnAssert() throws Exception {

        String jsonString = "{\n" +
                "  \"createPerson\": {\n" +
                "    \"request\":{\n" +
                "      \"id\" : \"10101\"\n" +
                "    },\n" +
                "    \"response\": {\n" +
                "      \"status\" : 201\n" +
                "    }\n" +
                "  },\n" +
                "  \"id\": \"100\", \n" +
                "  \"id2\": 100 \n" +
                "}";
        JsonNode jsonNode = mapper.readTree(jsonString);

        final JsonNode id = jsonNode.get("id");

        final JsonNode createPerson = jsonNode.get("createPerson");

        final JsonNode status = jsonNode.get("createPerson").get("response").get("status");

        final int idAsNumber = id.asInt();

        final String idAsString = id.asText();

        final JsonNode id2 = jsonNode.get("id2");

    }

    @Test
    public void test_ignoreStepFailuresField() throws Exception {
        String jsonDocumentAsString = smartUtils
                .getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/04_ignoreStepFailures_in_multistep.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        assertThat(scenarioSpec, notNullValue());
        assertThat(scenarioSpec.getSteps().size(), is(2));
        assertThat(scenarioSpec.getIgnoreStepFailures(), is(true));
    }

    @Test
    public void testSerDe_parameterized() throws Exception {
        String jsonDocumentAsString = smartUtils
                .getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/09_scenario_parameterized.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        assertThat(scenarioSpec.getParameterized().getValueSource(), hasItem("hello"));
        assertThat(scenarioSpec.getParameterized().getCsvSource(), hasItem("1,        2,        200"));
    }
}