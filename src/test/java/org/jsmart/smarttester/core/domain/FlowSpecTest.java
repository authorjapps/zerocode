package org.jsmart.smarttester.core.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class FlowSpecTest {
    private ObjectMapper mapper;

    @Before
    public void beforeMethod() throws Exception {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    @Test
    public void willDeserializeA_VanilaFlow() throws Exception {
        String jsonDocumentAsString = SmartUtils.getJsonDocumentAsString("smart_test_cases/02_test_json_flow_single_step.json", this);
        FlowSpec flowDeserialized = mapper.readValue(jsonDocumentAsString, FlowSpec.class);

        assertThat(flowDeserialized, notNullValue());
        assertThat(flowDeserialized.getSteps().size(), is(1));
        assertThat(flowDeserialized.getFlowName(), containsString("Given_When_Then-Flow"));
    }

    @Test
    public void willDeserializeA_MultiSteps() throws Exception {
        String jsonDocumentAsString = SmartUtils.getJsonDocumentAsString("smart_test_cases/03_test_json_flow_multi_step.json", this);
        FlowSpec flowDeserialized = mapper.readValue(jsonDocumentAsString, FlowSpec.class);

        assertThat(flowDeserialized, notNullValue());
        assertThat(flowDeserialized.getSteps().size(), is(2));
        assertThat(flowDeserialized.getFlowName(), containsString("Given_When_Then-Flow"));
        assertThat(flowDeserialized.getSteps().get(1).getUrl(), containsString("/url2/path"));
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