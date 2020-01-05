package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import com.univocity.parsers.csv.CsvParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.di.provider.CsvParserProvider.LINE_SEPARATOR;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;

@RunWith(JukitoRunner.class)
// Or use - @UseModules(ApplicationMainModule.class)
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

    @Inject
    private CsvParser csvParser;

    @Test
    public void shouldDeserializeSingleStep() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/01_test_json_single_step.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);
        assertThat(stepDeserialized, notNullValue());
        final String requestJsonAsString = stepDeserialized.getRequest().toString();
        assertThat(requestJsonAsString, containsString("firstName"));

        Object queryParams = JsonPath.read(requestJsonAsString, "$.queryParams");
        Object requestHeaders = JsonPath.read(requestJsonAsString, "$.headers");

        assertThat(queryParams.toString(), is("{param1=value1, invId=10101}"));
        assertThat(requestHeaders.toString(), is("{Content-Type=application/json;charset=UTF-8, Cookie=cookie_123}"));

        Map<String, Object> headerMap = (HashMap) requestHeaders;
        Map<String, Object> queryParamsMap = (HashMap) queryParams;

        assertThat(headerMap.get("Cookie"), is("cookie_123"));
        assertThat(queryParamsMap.get("invId"), is(10101));
        assertThat(stepDeserialized.getAssertions().get("status").asInt(), is(201));
        assertThat(stepDeserialized.getAssertions().get("status").asLong(), is(201L));
        assertThat(stepDeserialized.getAssertions().get("status").asText(), is("201"));
    }

    @Test
    public void testVerifications_section() throws Exception {
        String jsonDocumentAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/00_test_json_single_step_verifications.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);

        assertThat(stepDeserialized.getVerify().get("status").asText(), is("201"));
        assertThat(stepDeserialized.getAssertions().get("status").asText(), is("201"));
        assertThat(stepDeserialized.getVerifyMode(), is("STRICT"));
    }

    @Test
    public void testValidators_array() throws Exception {
        String jsonDocumentAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/00_test_json_single_step_verifications.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);

        assertThat(stepDeserialized.getValidators().get(0).getField(), is("fooFoo"));
        assertThat(stepDeserialized.getValidators().get(0).getValue().asText(), is("barBar"));
    }

    @Test
    public void testParameterized_values() throws Exception {
        String jsonDocumentAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/06_test_single_step_parameterized_value.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);

        assertThat(stepDeserialized.getParameterized().get(0), is("Hello"));
        assertThat(stepDeserialized.getParameterized().get(1), is(123));
        assertThat(stepDeserialized.getParameterized().get(2), is(true));
    }

    @Test
    public void testParameterized_csv() throws Exception {

        String jsonDocumentAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/07_test_single_step_parameterized_csv.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);

        List<String> parameterizedCsv = stepDeserialized.getParameterizedCsv();
        String[] parsedLine0 = csvParser.parseLine(parameterizedCsv.get(0) + LINE_SEPARATOR);
        String[] parsedLine1 = csvParser.parseLine(parameterizedCsv.get(1) + LINE_SEPARATOR);

        assertThat(parsedLine0[0], is("1"));
        assertThat(parsedLine0[1], is("2"));
        assertThat(parsedLine0[2], is("3"));

        assertThat(parsedLine1[0], is("11"));
        assertThat(parsedLine1[1], is("22"));
        assertThat(parsedLine1[2], is("33"));
    }

    @Test
    public void testDeserExternalStepFile() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/05_test_external_step_reuse.json");
        Step stepDeserialized = mapper.readValue(jsonDocumentAsString, Step.class);
        assertThat(stepDeserialized, notNullValue());
        assertThat(stepDeserialized.getId(), is("step1"));
        assertThat(stepDeserialized.getStepFile().asText(), is("file/path"));
    }

    @Test
    public void shouldSerializeSingleStep() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/01_test_json_single_step.json");
        Step stepJava = mapper.readValue(jsonDocumentAsString, Step.class);

        JsonNode singleStepNode = mapper.valueToTree(stepJava);
        String singleStepNodeString = mapper.writeValueAsString(singleStepNode);

        JSONAssert.assertEquals(jsonDocumentAsString, singleStepNodeString, false);

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

    @Test
    public void shouldSerializeSingleStep_method() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/01.1_test_json_single_step_method.json");
        Step stepJava = mapper.readValue(jsonDocumentAsString, Step.class);

        JsonNode singleStepNode = mapper.valueToTree(stepJava);
        String singleStepNodeString = mapper.writeValueAsString(singleStepNode);

        JSONAssert.assertEquals(jsonDocumentAsString, singleStepNodeString, false);
        assertThat(singleStepNode.get("method").asText(), is("POST"));
    }

    @Test
    public void testPayLoad_strictPass() {
        String actual = "{\n" +
                "    \"id\": 123,\n" +
                "    \"type\": \"Premium High Value\",\n" +
                "    \"office\":{\n" +
                "        \"type\": \"branches\",\n" +
                "        \"locations\":[\n" +
                "            {\n" +
                "                \"country\": \"UK\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"addresses\": [\n" +
                "        {\n" +
                "            \"type\": \"home\",\n" +
                "            \"line1\": \"10 Random St\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String expected = "{\n" +
                //"    \"id\": 123,\n" +
                "    \"type\": \"Premium High Value\",\n" +
                "    \"office\":{\n" +
                "        \"type\": \"branches\",\n" +
                "        \"locations\":[\n" +
                "            {\n" +
                "                \"country\": \"UK\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"addresses\": [\n" +
                "        {\n" +
                "            \"type\": \"home\",\n" +
                "            \"line1\": \"10 Random St\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        try {
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);

        } catch (Throwable ex) {
            assertThat(ex.getMessage(), containsString("Unexpected: id"));
        }
    }

    @Test
    public void testPayLoad_strictFail_FieldAssertion() {
        List<FieldAssertionMatcher> matchers = new ArrayList<>();

        String actual = "{\n" +
                "    \"office\":{\n" +
                "        \"type\": \"branches\",\n" +
                "        \"locations\":[\n" +
                "            {\n" +
                "                \"country\": \"UK\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"exactMatches\": true,\n" +
                "    \"foo\": \"Mr FooX\",\n" +
                "    \"name\": \"Mr Bean\",\n" +
                "    \"city\": \"Lon\",\n" +
                "    \"listings\":[\n" +
                "        {\n" +
                "            \"exchange\": \"NYX\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String expected = "{\n" +
                "    \"office\":{\n" +
                "        \"type\": \"branches\",\n" +
                "        \"locations\":[\n" +
                "            {\n" +
                "                \"country\": \"FRANCE\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                //"    \"exactMatches\": true,\n" +
                "    \"foo\": \"Mr FooX\",\n" +
                "    \"city\": \"Lon\",\n" +
                "    \"listings\":[\n" +
                "        {\n" +
                "            \"exchange\": \"NYX\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"addresses\":[\n" +
                "        {\n" +
                "            \"type\": \"office\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        try {

            JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);

        } catch (AssertionError ex) {
            String message = ex.getMessage();
            List<String> errorMsgs = Arrays.asList(message.split(";"));
            matchers = errorMsgs.stream()
                    .map(msg -> {
                        List<String> strings = Arrays.asList(msg.trim().split("\n"));
                        String fieldJsonPath = "";
                        if(strings != null && strings.size() > 0){
                            fieldJsonPath = strings.get(0).substring(strings.get(0).indexOf(": ") + 1).trim();
                        }
                        if (strings.size() == 1) {
                            return aNotMatchingMessage(fieldJsonPath, "", strings.get(0).trim());
                        } else if (strings.size() == 2) {
                            return aNotMatchingMessage(fieldJsonPath, "", strings.get(1).trim());
                        } else if (strings.size() > 2) {
                            return aNotMatchingMessage(fieldJsonPath, strings.get(1).trim(), strings.get(2).trim());
                        } else {
                            return aMatchingMessage();
                        }
                    })
                    .collect(Collectors.toList());
        }

        assertThat(matchers.size(), is(4));
        assertThat(matchers.get(0).toString(), is("Assertion jsonPath 'addresses' with actual value 'but none found' did not match the expected value ''"));
        assertThat(matchers.get(1).toString(), is("Assertion jsonPath 'office.locations[0].country' with actual value 'got: UK' did not match the expected value 'Expected: FRANCE'"));
        assertThat(matchers.get(2).toString(), is("Assertion jsonPath 'exactMatches' with actual value 'Unexpected: exactMatches' did not match the expected value ''"));
        assertThat(matchers.get(3).toString(), is("Assertion jsonPath 'name' with actual value 'Unexpected: name' did not match the expected value ''"));
    }

}