package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.containsString;
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
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("01_test_smart_test_cases/02_test_json_flow_single_step.json");
        ScenarioSpec flowDeserialized = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        assertThat(flowDeserialized, notNullValue());
        assertThat(flowDeserialized.getSteps().size(), is(1));
        assertThat(flowDeserialized.getLoop(), is(5));
        assertThat(flowDeserialized.getScenarioName(), containsString("Given_When_Then-Flow"));
    }

    @Test
    public void willDeserializeA_MultiSteps() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("01_test_smart_test_cases/03_test_json_flow_multi_step.json");
        ScenarioSpec flowDeserialized = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        assertThat(flowDeserialized, notNullValue());
        assertThat(flowDeserialized.getSteps().size(), is(2));
        assertThat(flowDeserialized.getScenarioName(), containsString("Given_When_Then-Flow"));
        assertThat(flowDeserialized.getSteps().get(1).getUrl(), containsString("/url2/path"));
    }

    @Test
    public void shouldSerializeSingleFlow() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("01_test_smart_test_cases/03_test_json_flow_multi_step.json");
        ScenarioSpec scenarioSpec = mapper.readValue(jsonDocumentAsString, ScenarioSpec.class);

        JsonNode flowSpecNode = mapper.valueToTree(scenarioSpec);

        /**
         * Note:
         * jayway json assertEquals has issues if json doc has got comments. So find out how to ignore or allow comments
         */
        JSONAssert.assertEquals(flowSpecNode.toString(), jsonDocumentAsString, true);

        assertThat(flowSpecNode.get("scenarioName").asText(), containsString("Given_When_Then"));
        assertThat(flowSpecNode.get("loop").asInt(), is(5));

    }

    @Test
    @Ignore
    public void willComplainForDuplicateNames_Step() throws Exception {
        fail();
    }

    @Test
    @Ignore
    public void willComplainForDuplicateNames_Flow() throws Exception {
        fail();
    }

    @Test
    @Ignore
    public void willReadAllJsonFiles_AND_Complain_for_Duplicate_names() throws Exception {
        fail();
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
}