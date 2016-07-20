package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
//@UseModules(ApplicationMainModule.class)
public class StepTest {
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
        /**
         * This also tests your injection wirings.
         * Running via JukitoRunner doesnt need this.
         */
        //mapper = new ObjectMapper();

    }

    @Test
    public void shouldDeserializeSingleStep() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("01_test_smart_test_cases/01_test_json_single_step.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);
        assertThat(stepDeserialized, notNullValue());
        final String requestJsonAsString = stepDeserialized.getRequest().toString();
        assertThat(requestJsonAsString, containsString("firstName"));

        Object queryParams = JsonPath.read(requestJsonAsString, "$.queryParams");
        Object requestHeaders = JsonPath.read(requestJsonAsString, "$.headers");

        assertThat(queryParams.toString(), is("{param1=value1, invId=10101}"));
        assertThat(requestHeaders.toString(), is("{Content-Type=application/json;charset=UTF-8, Cookie=cookie_123}"));

        //Map<String, Object> headerMap = smartUtils.readJsonStringAsMap(requestHeaders.toString());
        //Map<String, Object> queryParamsMap = smartUtils.readJsonStringAsMap(queryParams.toString());

        Map<String, Object> headerMap = (HashMap)requestHeaders;
        Map<String, Object> queryParamsMap = (HashMap)queryParams;

        assertThat(headerMap.get("Cookie"), is("cookie_123"));
        assertThat(queryParamsMap.get("invId"), is(10101));
        //assertThat(stepDeserialized.getAssertions().getStatus(), is(201));
        assertThat(stepDeserialized.getAssertions().get("status").asInt(), is(201));
        assertThat(stepDeserialized.getAssertions().get("status").asLong(), is(201L));
        assertThat(stepDeserialized.getAssertions().get("status").asText(), is("201"));
    }

    @Test
    public void shouldSerializeSingleStep() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("01_test_smart_test_cases/01_test_json_single_step.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);

        JsonNode singleStepNode = mapper.valueToTree(stepDeserialized);
        String singleStepNodeString = mapper.writeValueAsString(singleStepNode);

        // jayway json assert with this string.
        JSONAssert.assertEquals(singleStepNodeString, jsonDocumentAsString, true);

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

    /**
     * Do not use this private method as this has been moved to SmartUtils
     **/
    protected String getJsonDocumentAsString(String name) throws IOException {
        String jsonAsString = Resources.toString(getClass().getClassLoader().getResource(name), StandardCharsets.UTF_8);
        return jsonAsString;
    }
}