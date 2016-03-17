package org.jsmart.smarttester.core.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.smarttester.core.di.SmartServiceModule;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
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
@UseModules(SmartServiceModule.class)
public class FlowSpecTest {
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

    @Test
    public void willDeserializeA_VanilaFlow() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("smart_test_cases/02_test_json_flow_single_step.json");
        FlowSpec flowDeserialized = mapper.readValue(jsonDocumentAsString, FlowSpec.class);

        System.out.println(flowDeserialized);

        assertThat(flowDeserialized, notNullValue());
        assertThat(flowDeserialized.getSteps().size(), is(1));
        assertThat(flowDeserialized.getLoop(), is(5));
        assertThat(flowDeserialized.getFlowName(), containsString("Given_When_Then-Flow"));
    }

    @Test
    public void willDeserializeA_MultiSteps() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("smart_test_cases/03_test_json_flow_multi_step.json");
        FlowSpec flowDeserialized = mapper.readValue(jsonDocumentAsString, FlowSpec.class);

        assertThat(flowDeserialized, notNullValue());
        assertThat(flowDeserialized.getSteps().size(), is(2));
        assertThat(flowDeserialized.getFlowName(), containsString("Given_When_Then-Flow"));
        assertThat(flowDeserialized.getSteps().get(1).getUrl(), containsString("/url2/path"));
    }

    @Test
    public void shouldSerializeSingleFlow() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("smart_test_cases/03_test_json_flow_multi_step.json");
        FlowSpec flowSpec = mapper.readValue(jsonDocumentAsString, FlowSpec.class);

        JsonNode flowSpecNode = mapper.valueToTree(flowSpec);

        /**
         * Note:
         * jayway json assertEquals has issues if json doc has got comments. So find out how to ignore or allow comments
         */
        JSONAssert.assertEquals(flowSpecNode.toString(), jsonDocumentAsString, true);

        assertThat(flowSpecNode.get("flowName").asText(), containsString("Given_When_Then"));
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
}