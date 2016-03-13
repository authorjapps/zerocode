package org.jsmart.smarttester.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class StepTest {
    private ObjectMapper mapper;

    @Before
    public void beforeMethod() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldDeserializeSingleStep() throws Exception {
        String jsonDocumentAsString = SmartUtils.getJsonDocumentAsString("smart_test_cases/01_test_json_single_step.json", this);
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);
        assertThat(stepDeserialized, notNullValue());
        assertThat(stepDeserialized.getRequest().getBody().toString(), containsString("externalHandleSpace"));
        assertThat(stepDeserialized.getRequest().getHeaders().get("Cookie"), is("cookie_123"));
        assertThat(stepDeserialized.getRequest().getQueryParams().get("invId"), is(10101));
        assertThat(stepDeserialized.getAssertions().getStatus(), is(201));
    }

    @Test
    public void shouldSerializeSingleStep() throws Exception {
        String jsonDocumentAsString = SmartUtils.getJsonDocumentAsString("smart_test_cases/01_test_json_single_step.json", this);
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);

        JsonNode singleStepNode = mapper.valueToTree(stepDeserialized);
        System.out.println(singleStepNode);

        // jayway json assert with this string.
        JSONAssert.assertEquals(singleStepNode.toString(), jsonDocumentAsString, true);

        assertThat(singleStepNode.get("name").asText(), is("StepNameWithoutSpaceEgCREATE"));
        assertThat(singleStepNode.get("loop").asInt(), is(3));
        assertThat(singleStepNode.get("operation").asText(), is("POST"));
        assertThat(singleStepNode.get("request").get("headers").toString(), containsString("Content-Type"));
        assertThat(singleStepNode.get("request").get("queryParams").toString(), containsString("param1"));
        assertThat(singleStepNode.get("request").get("queryParams").toString(), is("{\"param1\":\"value1\",\"invId\":10101}"));

        /**
         * Note:
         * if it is direct value, then read as e.g. asInt, asBoolean etc,
         * If it is a JSON block, then read as jsonNode.toString, do not use asText()
         * e.g.
         * 1. singleStepNode.toString, -->Correct
         * 2. singleStepNode.get("request").get("headers").toString() -->Correct
         * 2.1. singleStepNode.get("request").get("headers").asText() -->Wrong
         * 3. singleStepNode.get("name").asText() -->Correct
         *
         */
    }

    /** Do not use this private method as this has been moved to SmartUtils **/
    protected String getJsonDocumentAsString(String name) throws IOException {
        String jsonAsString = Resources.toString(getClass().getClassLoader().getResource(name), StandardCharsets.UTF_8);
        return jsonAsString;
    }
}