package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.core.Is;
import org.jsmart.zerocode.TestUtility;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.provider.JsonPathJacksonProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.read;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.jsmart.zerocode.core.utils.SmartUtils.checkDigNeeded;
import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class ZeroCodeAssertionsProcessorImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Injector injector;
    SmartUtils smartUtils;
    ObjectMapper mapper;

    ZeroCodeAssertionsProcessorImpl jsonPreProcessor;

    @Before
    public void setUpStuff() throws Exception {
        String serverEnvFileName = "config_hosts_test.properties";
        injector = Guice.createInjector(new ApplicationMainModule(serverEnvFileName));
        smartUtils = injector.getInstance(SmartUtils.class);
        mapper = new ObjectMapperProvider().get();
        Configuration.setDefaults(new JsonPathJacksonProvider().get());
        jsonPreProcessor =
                new ZeroCodeAssertionsProcessorImpl(smartUtils.getMapper(), serverEnvFileName);
    }

    @Test
    public void willEvaluatePlaceHolder() throws Exception {

        String aString = "Hello_${WORLD}";
        List<String> placeHolders = getTestCaseTokens(aString);
        assertThat(placeHolders.size(), is(1));
        assertThat(placeHolders.get(0), is("WORLD"));

        aString = "Hello_${$.step_name}";
        placeHolders = getTestCaseTokens(aString);
        assertThat(placeHolders.size(), is(1));
        assertThat(placeHolders.get(0), is("$.step_name"));
    }

    @Test
    public void willResolveWithParamMap() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/test_engine/01_request_with_place_holders.json", ScenarioSpec.class);
        final String requestJsonAsString = scenarioSpec.getSteps().get(0).getRequest().toString();

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);

        String lastName = JsonPath.read(resolvedRequestJson, "$.body.Customer.lastName");
        String nickName = JsonPath.read(resolvedRequestJson, "$.body.Customer.nickName");

        assertNotEquals(lastName, nickName);
    }

    @Test
    public void willCaptureAllPlaceHolders() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/test_engine/01_request_with_place_holders.json", ScenarioSpec.class);
        final String requestJsonAsString = scenarioSpec.getSteps().get(0).getRequest().toString();

        final List<String> placeHolders = getTestCaseTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(4));

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson, containsString("\"staticName\":\"abcde\""));

        String specAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/test_engine/01_request_with_place_holders.json");
        final String resolvedSpecString =
                jsonPreProcessor.resolveStringJson(specAsString, specAsString);
        assertThat(resolvedSpecString, containsString("\"url\": \"/persons/abc\""));
    }

    @Test
    public void willResolveJsonPathOfJayWay() throws Exception {
        String specAsString =
                smartUtils.getJsonDocumentAsString("unit_test_files/test_engine/01_request_with_place_holders.json");

        final List<String> jsonPaths = jsonPreProcessor.getAllJsonPathTokens(specAsString);
        assertThat(jsonPaths.size(), is(2));

        final String resolvedSpecWithPaths =
                jsonPreProcessor.resolveStringJson(specAsString, specAsString);
        assertThat(resolvedSpecWithPaths, containsString("\"staticName\": \"abcde\""));

        // final String resolvedSpecResolvedPaths =
        // jsonPreProcessor.resolveJsonPaths(resolvedSpecWithPaths);
        assertThat(resolvedSpecWithPaths, containsString("\"actualName\": \"${STATIC.ALPHABET:5}\""));
        assertThat(resolvedSpecWithPaths, containsString("\"actualNameSize\": \"2\""));
    }

    @Test
    public void willResolveJsonPathOfJayWayWith_SuppliedScenarioState() throws Exception {
        String specAsString =
                smartUtils.getJsonDocumentAsString(
                        "unit_test_files/test_engine/02_1_two_requests_with_json_path_assertion.json");

        final List<String> jsonPaths = jsonPreProcessor.getAllJsonPathTokens(specAsString);
        assertThat(jsonPaths.size(), is(3));

        String scenarioState =
                "{\n"
                        + "    \"step1\": {\n"
                        + "        \"request\": {\n"
                        + "            \"body\": {\n"
                        + "                \"customer\": {\n"
                        + "                    \"firstName\": \"FIRST_NAME\",\n"
                        + "                    \"staticName\": \"ANOTHER_NAME\",\n"
                        + "                    \"addresses\":[\"office-1\", \"home-2\"]\n"
                        + "                }\n"
                        + "            }\n"
                        + "        },\n"
                        + "        \"response\": {\n"
                        + "            \"id\": 10101\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";
        final String resolvedSpecWithPaths =
                jsonPreProcessor.resolveStringJson(specAsString, scenarioState);
        assertThat(resolvedSpecWithPaths, containsString("\"staticName\": \"abcde\""));
        assertThat(resolvedSpecWithPaths, containsString("\"firstName\": \"FIRST_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"firstName2\": \"FIRST_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"actualName\": \"ANOTHER_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"noOfAddresses\": \"2\""));
    }

    @Test
    public void willResolveAndTypeCast_SingleDimentionArrayElements_FromScenarioState() throws Exception {
        String specAsString =
                smartUtils.getJsonDocumentAsString(
                        "unit_test_files/test_engine/02_2_resolve_typecast_in_single_dimention_arraylist_assertion.json");

        final List<String> jsonPaths = jsonPreProcessor.getAllJsonPathTokens(specAsString);
        assertThat(jsonPaths.size(), is(6));

        String scenarioState =
                "{\n"
                        + "    \"step1\": {\n"
                        + "        \"request\": {\n"
                        + "            \"body\": {\n"
                        + "                \"customer\": {\n"
                        + "\"ids\": [\n" +
                        "              10101,\n" +
                        "              10102\n" +
                        "            ],"
                        + "                    \"firstName\": \"FIRST_NAME\",\n"
                        + "                    \"staticName\": \"ANOTHER_NAME\",\n"
                        + "                    \"addresses\":[\"office-1\", \"home-2\"]\n"
                        + "                }\n"
                        + "            }\n"
                        + "        },\n"
                        + "        \"response\": {\n"
                        + "            \"id\": 10101\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";
        final String resolvedSpecWithPaths =
                jsonPreProcessor.resolveStringJson(specAsString, scenarioState);
        System.out.println("resolvedSpecWithPaths ==> " + resolvedSpecWithPaths);

        Object jsonPathValue = JsonPath.read(resolvedSpecWithPaths,
                "$.steps[1].request.body.Customer.accounts[0]");

        assertThat(jsonPathValue.getClass().getName(), is("java.lang.String"));

        assertThat(resolvedSpecWithPaths, containsString("\"staticName\":\"abcde\""));
        assertThat(resolvedSpecWithPaths, containsString("\"firstName\":\"FIRST_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"firstName2\":\"FIRST_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"actualName\":\"ANOTHER_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"noOfAddresses\":\"2\""));
        assertThat(resolvedSpecWithPaths, containsString("\"accounts\":[\"10101\",\"10102\"]"));
    }

    @Test
    public void willResolveJsonPathOfJayWayFor_AssertionSection() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/test_engine/02_1_two_requests_with_json_path_assertion.json", ScenarioSpec.class);

        // Get the 2nd step
        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(1).getAssertions().toString();
        String scenarioState =
                "{\n"
                        + "    \"step1\": {\n"
                        + "        \"request\": {\n"
                        + "            \"body\": {\n"
                        + "                \"customer\": {\n"
                        + "                    \"firstName\": \"FIRST_NAME\",\n"
                        + "                    \"staticName\": \"ANOTHER_NAME\",\n"
                        + "                    \"addresses\":[\"office-1\", \"home-2\"]\n"
                        + "                }\n"
                        + "            }\n"
                        + "        },\n"
                        + "        \"response\": {\n"
                        + "            \"id\": 10101\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, scenarioState);
        assertThat(resolvedAssertions, containsString("\"actualName\":\"ANOTHER_NAME\""));

        // start assertion
        String sapmleExecutionResult =
                smartUtils.getJsonDocumentAsString(
                        "unit_test_files/test_engine/02_2_sample_resolved_execution_response.json");
        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(17));

        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, sapmleExecutionResult);

        System.out.println("###failedReports : " + failedReports);
        assertThat(
                failedReports.toString(), containsString("did not match the expected value 'NOT NULL'"));
        assertThat(
                failedReports.toString(),
                containsString("did not match the expected value 'ANOTHER_NAME'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'NULL'"));
        assertThat(failedReports.toString(), containsString("citizenship' with actual value '[{"));
        assertThat(failedReports.toString(), containsString("personalities' with actual value 'null'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value '[]'"));
        assertThat(failedReports.toString(), not(containsString("pastActivities")));
        assertThat(
                failedReports.toString(),
                containsString("did not match the expected value 'Array of size 5'"));
        assertThat(
                failedReports.toString(),
                containsString("did not match the expected value 'Array of size 4'"));
        assertThat(
                failedReports.toString(),
                containsString("did not match the expected value 'containing sub-string:DaddyWithMac'"));
        assertThat(
                failedReports.toString(),
                containsString("did not match the expected value 'Greater Than:499'"));
        assertThat(
                failedReports.toString(),
                containsString("'null' did not match the expected value 'Greater Than:388'"));
        assertThat(
                failedReports.toString(),
                containsString("actual value '1400' did not match the expected value 'Lesser Than:1300'"));
        assertThat(failedReports.size(), is(11));
    }

    @Test
    public void willResolveTextNodeFor_Assertion() throws Exception {

        final String assertionsSectionTextNodeAsString = "\"id-generated-0101\"";

        String scenarioState =
                "{\n"
                        + "    \"step1\": {\n"
                        + "        \"request\": {\n"
                        + "            \"body\": {\n"
                        + "                \"customer\": {\n"
                        + "                    \"firstName\": \"FIRST_NAME\",\n"
                        + "                    \"staticName\": \"ANOTHER_NAME\",\n"
                        + "                    \"addresses\":[\"office-1\", \"home-2\"]\n"
                        + "                }\n"
                        + "            }\n"
                        + "        },\n"
                        + "        \"response\": \"id-generated-0101-aga-baga\""
                        + // <--- In this case this is not relevant as this path is not used
                        "    }\n"
                        + "}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionTextNodeAsString, scenarioState);
        assertThat(resolvedAssertions, containsString("\"id-generated-0101\""));

        // start assertion
        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(1));

        String sampleExecutionResult = "\"id-generated-0101-XY\"";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, sampleExecutionResult);

        System.out.println("###failedReports : " + failedReports);
        assertThat(
                failedReports.toString(),
                containsString(
                        "'$' with actual value 'id-generated-0101-XY' did not match the expected value 'id-generated-0101'"));
        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void willResolveIntegerNodeFor_Assertion() throws Exception {

        final Integer assertionsSectionInt = 1099;

        String scenarioState =
                "{\n"
                        + "    \"step1\": {\n"
                        + "        \"request\": {\n"
                        + "            \"body\": {\n"
                        + "                \"customer\": {\n"
                        + "                    \"firstName\": \"FIRST_NAME\",\n"
                        + "                    \"staticName\": \"ANOTHER_NAME\",\n"
                        + "                    \"addresses\":[\"office-1\", \"home-2\"]\n"
                        + "                }\n"
                        + "            }\n"
                        + "        },\n"
                        + "        \"response\": 300000"
                        + // <--- In this case this is not relevant as this path is not used
                        "    }\n"
                        + "}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionInt.toString(), scenarioState);
        assertThat(resolvedAssertions, containsString("1099"));

        // start assertion
        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(1));

        Integer sampleExecutionResult = 1077;
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, sampleExecutionResult.toString());

        System.out.println("###failedReports : " + failedReports);
        assertThat(
                failedReports.toString(),
                containsString("'$' with actual value '1077' did not match the expected value '1099'"));
        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void testLocalDate_formatter() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/localdatetime/00_local_date_time_place_holders_unit_test.json", ScenarioSpec.class);

        final String requestJsonAsString = scenarioSpec.getSteps().get(0).getRequest().toString();

        final List<String> placeHolders = getTestCaseTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(2));

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson.indexOf("LOCAL.DATE.TODAY:"), is(-1));
        assertThat(resolvedRequestJson.indexOf("${LOCAL.DATE.TODAY:yyyy}"), is(-1));
    }

    @Test
    public void testLocalDateTime_formatter() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/localdatetime/00_local_date_time_place_holders_unit_test.json", ScenarioSpec.class);

        final String requestJsonAsString = scenarioSpec.getSteps().get(0).getRequest().toString();

        final List<String> placeHolders = getTestCaseTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(2));

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson.indexOf("LOCAL.DATETIME.NOW:"), is(-1));
    }

    @Test
    public void testRandom_UUID() throws Exception {

        final String requestJsonAsString = "{\n" + "\t\"onlineOrderId\": \"${RANDOM.UUID}\"\n" + "}";

        final List<String> placeHolders = getTestCaseTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(1));

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson.indexOf("RANDOM.UUID"), is(-1));

        final HashMap<String, String> hashMap =
                smartUtils.getMapper().readValue(resolvedRequestJson, HashMap.class);

        assertThat(
                hashMap.get("onlineOrderId").length(),
                is(36)); // "onlineOrderId": "48c3b4ff-5078-40bb-8d62-11abcbdef5b3"
    }

    @Test
    public void testRandom_alpha() throws Exception {

        final String requestJsonAsString = "{\n" + "\t\"onlineOrderId\": \"${RANDOM.STRING:2}\"\n" + "}";

        final List<String> placeHolders = getTestCaseTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(1));

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson.indexOf("RANDOM.STRING:"), is(-1));

        final HashMap<String, String> hashMap =
                smartUtils.getMapper().readValue(resolvedRequestJson, HashMap.class);

        assertThat(hashMap.get("onlineOrderId").length(), is(2));
    }

    @Test
    public void testRandom_alphanumeric() throws Exception {

        final String requestJsonAsString = "{\n" + "\t\"onlineOrderId\": \"${RANDOM.ALPHANUMERIC:2}\"\n" + "}";

        final List<String> placeHolders = getTestCaseTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(1));

        final String resolvedRequestJson =
                jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson.indexOf("RANDOM.ALPHANUMERIC:"), is(-1));

        final HashMap<String, String> hashMap =
                smartUtils.getMapper().readValue(resolvedRequestJson, HashMap.class);

        assertThat(hashMap.get("onlineOrderId").length(), is(2));
    }

    @Test
    public void testIgnoreCaseWith_containsNoMatch() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/ignore_case/test_string_match_withIgnoring_case.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions, containsString("{\"name\":\"$CONTAINS.STRING.IGNORECASE:CReASY\"}}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "  \"status\": 201,\n"
                        + "  \"body\": {\n"
                        + "    \"name\": \"Hello CreXasy\"\n"
                        + "  }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.toString(),
                containsString(
                        "did not match the expected value 'containing sub-string with ignoring case:"));
    }

    @Test
    public void testIgnoreCaseWith_containsMatch() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/ignore_case/test_string_match_withIgnoring_case.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions, containsString("{\"name\":\"$CONTAINS.STRING.IGNORECASE:CReASY\"}}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "  \"status\": 201,\n"
                        + "  \"body\": {\n"
                        + "    \"name\": \"Hello Creasy\"\n"
                        + "  }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testString_regexMatch() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/regex_match/string_matches_regex_test.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("{\"dob\":\"$MATCHES.STRING:\\\\d{4}-\\\\d{2}-\\\\d{2}\"}}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "  \"status\": 201,\n"
                        + "  \"body\": {\n"
                        + "    \"dob\": \"2018-06-26\"\n"
                        + "  }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testArraySize_numberOnly() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_number_only_test.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":2}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testArraySize_numberOnlyNegative() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_number_only_test.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":2}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void testArraySize_expressionGT() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_GT.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$GT.1\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testArraySize_expressionFailTest() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_fail_test_GT.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$GT.5\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.persons' with actual value '2' did not match the expected value 'Array of size $GT.5'"));
    }

    @Test
    public void testArraySize_expressionLT() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_LT.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$LT.3\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testArraySize_expressionFailLT() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_fail_LT.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$LT.1\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.persons' with actual value '2' did not match the expected value 'Array of size $LT.1'"));
    }

    @Test
    public void testArraySize_expressionEQ() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_EQ.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$EQ.2\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testArraySize_expressionFailEQ() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_fail_EQ.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$EQ.3\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.persons' with actual value '2' did not match the expected value 'Array of size $EQ.3'"));
    }

    @Test
    public void testArraySize_expressionNotEQ() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_NotEQ.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$NOT.EQ.3\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testArraySize_expressionFailNotEQ() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/array_size/array_size_expresssion_test_fail_NotEQ.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("{\"persons.SIZE\":\"$NOT.EQ.2\"}"));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 201,\n"
                        + "    \"body\": {\n"
                        + "        \"persons\": [\n"
                        + "            {\n"
                        + "                \"name\": \"Tom\"\n"
                        + "            },\n"
                        + "            {\n"
                        + "                \"name\": \"Mady\"\n"
                        + "            }\n"
                        + "        ]\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.persons' with actual value '2' did not match the expected value 'Array of size $NOT.EQ.2'"));
    }

    @Test
    public void testDateAfterBefore_both() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/date_after_before/dateAfterBefore_test_both.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"startDateTime\":\"$LOCAL.DATETIME.BEFORE:2015-09-14T09:49:34.000Z\","));
        assertThat(
                resolvedAssertions,
                containsString("\"endDateTime\":\"$LOCAL.DATETIME.AFTER:2015-09-14T09:49:34.000Z\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(3));

        String mockTestResponse =
                "{\n"
                        + "	\"status\": 200,\n"
                        + "	\"body\": {\n"
                        + "	    \"projectDetails\": {\n"
                        + "            \"startDateTime\": \"2014-09-14T09:49:34.000Z\",\n"
                        + "            \"endDateTime\": \"2016-09-14T09:49:34.000Z\"\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";

        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);
        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testDateAfterBefore_fail_both() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/date_after_before/dateAfterBefore_test_fail_both.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"startDateTime\":\"$LOCAL.DATETIME.BEFORE:2016-09-14T09:49:34.000Z\","));
        assertThat(
                resolvedAssertions,
                containsString("\"endDateTime\":\"$LOCAL.DATETIME.AFTER:2019-09-14T09:49:34.000Z\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(3));

        String mockTestResponse =
                "{\n"
                        + "	\"status\": 200,\n"
                        + "	\"body\": {\n"
                        + "	    \"projectDetails\": {\n"
                        + "	            \"startDateTime\": \"2017-04-14T11:49:56.000Z\",\n"
                        + "	            \"endDateTime\": \"2018-11-12T09:39:34.000Z\"\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";

        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(2));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.projectDetails.startDateTime' with actual value '2017-04-14T11:49:56.000Z' "
                                + "did not match the expected value 'Date Before:2016-09-14T09:49:34'"));
        assertThat(
                failedReports.get(1).toString(),
                is(
                        "Assertion jsonPath '$.body.projectDetails.endDateTime' with actual value '2018-11-12T09:39:34.000Z' "
                                + "did not match the expected value 'Date After:2019-09-14T09:49:34'"));
    }

    @Test
    public void testDateAfterBefore_fail_afterSameDate() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/date_after_before/dateAfterBefore_test_fail_afterSameDate.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"startDateTime\":\"$LOCAL.DATETIME.AFTER:2015-09-14T09:49:34.000Z\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "	\"status\": 200,\n"
                        + "	\"body\": {\n"
                        + "	    \"projectDetails\": {\n"
                        + "	            \"startDateTime\": \"2015-09-14T09:49:34.000Z\"\n"
                        + "        }\n"
                        + "    }\n"
                        + "}";

        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.projectDetails.startDateTime' with actual value '2015-09-14T09:49:34.000Z' "
                                + "did not match the expected value 'Date After:2015-09-14T09:49:34'"));
    }

    @Test
    public void testDateAfterBefore_fail_beforeSameDate() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava(
                        "unit_test_files/date_after_before/dateAfterBefore_test_fail_beforeSameDate.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"startDateTime\":\"$LOCAL.DATETIME.BEFORE:2015-09-14T09:49:34.000Z\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "	\"status\": 200,\n"
                        + "	\"body\": {\n"
                        + " 		\"projectDetails\": {\n"
                        + "			\"startDateTime\": \"2015-09-14T09:49:34.000Z\"\n"
                        + "		}\n"
                        + "	}\n"
                        + "}";

        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
        assertThat(
                failedReports.get(0).toString(),
                is(
                        "Assertion jsonPath '$.body.projectDetails.startDateTime' with actual value '2015-09-14T09:49:34.000Z' "
                                + "did not match the expected value 'Date Before:2015-09-14T09:49:34'"));
    }

    @Test
    public void testValueOneOf_ValuePresent() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/one_of/oneOf_test_currentStatus.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"currentStatus\":\"$ONE.OF:[Found, Searching, Not Looking]\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 200,\n"
                        + "    \"body\": {\n"
                        + "        \"currentStatus\": \"Searching\"\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testValueOneOf_ValueNotPresent() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/one_of/oneOf_test_currentStatus.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"currentStatus\":\"$ONE.OF:[Found, Searching, Not Looking]\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 200,\n"
                        + "    \"body\": {\n"
                        + "        \"currentStatus\": \"Quit\"\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void testValueOneOf_ActualResultNull() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/one_of/oneOf_test_currentStatus.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"currentStatus\":\"$ONE.OF:[Found, Searching, Not Looking]\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n" + "    \"status\": 200,\n" + "    \"body\": {\n" + "    }\n" + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void testValueOneOf_MatchEmptyString() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/one_of/oneOf_test_emptyString.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"currentStatus\":\"$ONE.OF:[Found, Searching,, Not Looking]\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 200,\n"
                        + "    \"body\": {\n"
                        + "        \"currentStatus\": \"\"\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testValueOneOf_MatchWhiteSpace() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/one_of/oneOf_test_whiteSpace.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(
                resolvedAssertions,
                containsString("\"currentStatus\":\"$ONE.OF:[Found, Searching, , Not Looking]\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 200,\n"
                        + "    \"body\": {\n"
                        + "        \"currentStatus\": \" \"\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(0));
    }

    @Test
    public void testValueOneOf_ExpectedArrayEmpty() throws Exception {
        ScenarioSpec scenarioSpec =
                smartUtils.scenarioFileToJava("unit_test_files/one_of/oneOf_test_expectedArrayEmpty.json", ScenarioSpec.class);

        final String assertionsSectionAsString =
                scenarioSpec.getSteps().get(0).getAssertions().toString();
        String mockScenarioState = "{}";

        final String resolvedAssertions =
                jsonPreProcessor.resolveStringJson(assertionsSectionAsString, mockScenarioState);
        assertThat(resolvedAssertions, containsString("\"currentStatus\":\"$ONE.OF:[]\""));

        List<JsonAsserter> asserters = jsonPreProcessor.createJsonAsserters(resolvedAssertions);
        assertThat(asserters.size(), is(2));

        String mockTestResponse =
                "{\n"
                        + "    \"status\": 200,\n"
                        + "    \"body\": {\n"
                        + "        \"currentStatus\": \"Searching\"\n"
                        + "    }\n"
                        + "}";
        List<FieldAssertionMatcher> failedReports =
                jsonPreProcessor.assertAllAndReturnFailed(asserters, mockTestResponse);

        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void testJsonPathValue_isArray() throws Exception {
        String scenarioStateJson =
                "{\n"
                        + "    \"type\": \"fuzzy\",\n"
                        + "    \"results\": [\n"
                        + "        {\n"
                        + "            \"id\": \"id-001\",\n"
                        + "            \"name\": \"Emma\"\n"
                        + "        },\n"
                        + "        {\n"
                        + "            \"id\": \"id-002\",\n"
                        + "            \"name\": \"Nikhi\"\n"
                        + "        }\n"
                        + "    ]\n"
                        + "}";
        Object jsonPathValue = JsonPath.read(scenarioStateJson, "$.results");
        assertThat(
                mapper.writeValueAsString(jsonPathValue),
                is("[{\"id\":\"id-001\",\"name\":\"Emma\"},{\"id\":\"id-002\",\"name\":\"Nikhi\"}]"));
        assertThat(jsonPreProcessor.isPathValueJson(jsonPathValue), is(true));
    }

    @Test
    public void testJsonPathValue_isObject() throws Exception {
        String scenarioStateJson =
                "{\n"
                        + "    \"type\": \"fuzzy\",\n"
                        + "    \"results\": [\n"
                        + "        {\n"
                        + "            \"id\": \"id-001\",\n"
                        + "            \"name\": \"Emma\"\n"
                        + "        },\n"
                        + "        {\n"
                        + "            \"id\": \"id-002\",\n"
                        + "            \"name\": \"Nikhi\"\n"
                        + "        }\n"
                        + "    ]\n"
                        + "}";
        Object jsonPathValue = JsonPath.read(scenarioStateJson, "$.results[0]");
        assertThat(
                mapper.writeValueAsString(jsonPathValue), is("{\"id\":\"id-001\",\"name\":\"Emma\"}"));
        assertThat(jsonPreProcessor.isPathValueJson(jsonPathValue), is(true));
    }

    @Test
    public void testJsonPathValue_isSingleField() {
        String scenarioStateJson =
                "{\n"
                        + "    \"type\": \"fuzzy\",\n"
                        + "    \"results\": [\n"
                        + "        {\n"
                        + "            \"id\": \"id-001\",\n"
                        + "            \"name\": \"Emma\"\n"
                        + "        },\n"
                        + "        {\n"
                        + "            \"id\": \"id-002\",\n"
                        + "            \"name\": \"Nikhi\"\n"
                        + "        }\n"
                        + "    ]\n"
                        + "}";
        Object jsonPathValue = JsonPath.read(scenarioStateJson, "$.type");
        assertThat(jsonPathValue + "", is("fuzzy"));
        assertThat(jsonPreProcessor.isPathValueJson(jsonPathValue), is(false));
    }

    @Test
    public void testLeafValuesArray_getIndexedElement() {

        Map<String, String> paramMap = new HashMap<>();
        String scenarioState = "{\n" +
                "    \"store\": {\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"category\": \"reference\",\n" +
                "                \"author\": \"Nigel Rees\",\n" +
                "                \"title\": \"Sayings of the Century\",\n" +
                "                \"price\": 8.95\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Evelyn Waugh\",\n" +
                "                \"title\": \"Sword of Honour\",\n" +
                "                \"price\": 12.99\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";


        String thisPath;
        thisPath = "$..author.$VALUE[0]";
        jsonPreProcessor.resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);
        assertThat(paramMap.get(thisPath), is("Nigel Rees"));

        thisPath = "$..author.$VALUE[1]";
        jsonPreProcessor.resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);
        assertThat(paramMap.get(thisPath), is("Evelyn Waugh"));
    }

    @Test
    public void testLeafValuesArray_VALUE() {

        Map<String, String> paramMap = new HashMap<>();
        String scenarioState = "{\n" +
                "    \"store\": {\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"category\": \"reference\",\n" +
                "                \"author\": \"Nigel Rees\",\n" +
                "                \"title\": \"Sayings of the Century\",\n" +
                "                \"price\": 8.95\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Evelyn Waugh\",\n" +
                "                \"title\": \"Sword of Honour\",\n" +
                "                \"price\": 12.99\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        String thisPath;
        thisPath = "$..author.$VALUE";
        jsonPreProcessor.resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);
        assertThat(paramMap.get(thisPath), is("Nigel Rees"));
    }

    @Test
    public void testLeafSingleValueArray_VALUE() {

        Map<String, String> paramMap = new HashMap<>();
        String scenarioState = "{\n" +
                "    \"store\": {\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"category\": \"reference\",\n" +
                "                \"author\": \"Nigel Rees\",\n" +
                "                \"title\": \"Sayings of the Century\",\n" +
                "                \"price\": 8.95\n" +
                "            }" +
                "        ]\n" +
                "    }\n" +
                "}";

        String thisPath;
        thisPath = "$..author.$VALUE";
        jsonPreProcessor.resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);
        assertThat(paramMap.get(thisPath), is("Nigel Rees"));
    }

    @Test
    public void testLeafValuesArray_badIndex() {

        Map<String, String> paramMap = new HashMap<>();
        String scenarioState = "{\n" +
                "    \"store\": {\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"category\": \"reference\",\n" +
                "                \"author\": \"Nigel Rees\",\n" +
                "                \"title\": \"Sayings of the Century\",\n" +
                "                \"price\": 8.95\n" +
                "            },\n" +
                "            {\n" +
                "                \"category\": \"fiction\",\n" +
                "                \"author\": \"Evelyn Waugh\",\n" +
                "                \"title\": \"Sword of Honour\",\n" +
                "                \"price\": 12.99\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        String thisPath;
        thisPath = "$..author.$VALUE[3]";

        expectedException.expectMessage("Index: 3, Size: 2");
        expectedException.expect(IndexOutOfBoundsException.class);
        jsonPreProcessor.resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);
    }

    @Test(expected = RuntimeException.class)
    public void test_wrongJsonPathBy_JSONCONTENT_Exception() throws JsonProcessingException {
        String jsonAsString = readJsonAsString("unit_test_files/json_content_unit_test/json_step_test_wrong_json_path.json");
        Map<String, Object> map = mapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() {});

        jsonPreProcessor.digReplaceContent(map, new ScenarioExecutionState());
    }

    @Test
    public void test_JSONCONTENT_leafNode() throws IOException {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        final StepExecutionState step1 =  createStepWithRequestAndResponse("create_emp", "\"body\" : {\n    \"id\" : 39001,\n    \"ldapId\" : \"emmanorton\"\n  }\n}\n  }");
        scenarioExecutionState.addStepState(step1);

        ScenarioSpec scenarioSpec =
            smartUtils.scenarioFileToJava(
                "unit_test_files/json_content_unit_test/json_step_test_json_content.json", ScenarioSpec.class);
        Step thisStep = scenarioSpec.getSteps().get(1);
        JsonNode stepNode = mapper.convertValue(thisStep, JsonNode.class);
        Map<String, Object> map = mapper.readValue(stepNode.toString(), new TypeReference<Map<String, Object>>() {});

        jsonPreProcessor.digReplaceContent(map, scenarioExecutionState);

        String jsonResult = mapper.writeValueAsString(map);

        assertThat(JsonPath.read(jsonResult, "$.request.body.addressId"), is(39001));
    }


    @Test
    public void test_JSONCONTENT_stringArray() throws IOException {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        final StepExecutionState step1 =  createStepWithRequestAndResponse("create_emp", "\"body\": {\"id\": 38001,\n     \"names\": [\"test1\", \"test2\"]\n}");
        scenarioExecutionState.addStepState(step1);

        ScenarioSpec scenarioSpec =
            smartUtils.scenarioFileToJava(
                "unit_test_files/json_content_unit_test/json_step_test_json_content_array.json", ScenarioSpec.class);
        Step thisStep = scenarioSpec.getSteps().get(1);
        JsonNode stepNode = mapper.convertValue(thisStep, JsonNode.class);
        Map<String, Object> map = mapper.readValue(stepNode.toString(), new TypeReference<Map<String, Object>>() {});

        jsonPreProcessor.digReplaceContent(map, scenarioExecutionState);

        String jsonResult = mapper.writeValueAsString(map);

        String result = "[\"test1\",\"test2\"]";

        Object jsonPathValue = JsonPath.parse(jsonResult).read("$.request.body.names");

        Assert.assertEquals(result, this.mapper.writeValueAsString(jsonPathValue));
    }

    @Test
    public void test_JSONCONTENT_objectArray() throws IOException {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();
        /**
         * {
         *     "id": 38001,
         *     "allAddresses": [
         *         {
         *             "type": "Home",
         *             "line1": "North Lon",
         *             "id": 43
         *         },
         *         {
         *             "type": "Office",
         *             "line1": "Central Lon"
         *         }
         *     ]
         * }
         */
        final StepExecutionState step1 =  createStepWithRequestAndResponse("create_emp",
                "\"body\": {\"id\": 38001, \"allAddresses\": [{\"type\": \"Home\", \"line1\": \"North Lon\", \"id\": 47}, {\"type\": \"Office\", \"line1\": \"Central Lon\"}]}");
        scenarioExecutionState.addStepState(step1);

        ScenarioSpec scenarioSpec =
            smartUtils.scenarioFileToJava(
                "unit_test_files/json_content_unit_test/json_step_test_json_content_objectarray.json", ScenarioSpec.class);

        Step thisStep = scenarioSpec.getSteps().get(1);
        JsonNode stepNode = mapper.convertValue(thisStep, JsonNode.class);
        Map<String, Object> map = mapper.readValue(stepNode.toString(), new TypeReference<Map<String, Object>>() {});

        jsonPreProcessor.digReplaceContent(map, scenarioExecutionState);

        String jsonResult = mapper.writeValueAsString(map);

        assertThat(JsonPath.read(jsonResult, "$.request.body.allAddresses[0].id"), is(47));
        assertThat(JsonPath.read(jsonResult, "$.request.body.allAddresses[0].type"), is("Home"));
        assertThat(JsonPath.read(jsonResult, "$.request.body.allAddresses[1].type"), is("Office"));
        assertThat(JsonPath.read(jsonResult, "$.request.body.allAddresses[0].line1"), is("North Lon"));
        assertThat(JsonPath.read(jsonResult, "$.request.body.allAddresses[1].line1"), is("Central Lon"));
    }

    @Test
    public void test_JSONCONTENT_jsonBlock() throws IOException {
        ScenarioExecutionState scenarioExecutionState = new ScenarioExecutionState();

        final StepExecutionState step1 =  createStepWithRequestAndResponse("create_emp",
                "\"body\": {\n" +
                        "    \"id\": 38001,\n" +
                        "    \"address\": {\n" +
                        "        \"type\": \"Home\",\n" +
                        "        \"line1\": \"River Side\"\n" +
                        "    }\n" +
                        "}");
        scenarioExecutionState.addStepState(step1);

        ScenarioSpec scenarioSpec =
            smartUtils.scenarioFileToJava(
                "unit_test_files/json_content_unit_test/json_step_test_json_content_block.json", ScenarioSpec.class);
        Step thisStep = scenarioSpec.getSteps().get(1);
        JsonNode stepNode = mapper.convertValue(thisStep, JsonNode.class);
        Map<String, Object> map = mapper.readValue(stepNode.toString(), new TypeReference<Map<String, Object>>() {});

        jsonPreProcessor.digReplaceContent(map, scenarioExecutionState);

        String jsonResult = mapper.writeValueAsString(map);

        assertThat(JsonPath.read(jsonResult, "$.request.body.address.type"), is("Home"));
        assertThat(JsonPath.read(jsonResult, "$.request.body.address.line1"), is("River Side"));
    }

    @Test
    public void test_NoJSONContentCheckDigNeeded() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/json_content_unit_test/json_step_no_json_content_test.json");
        Step step = mapper.readValue(jsonAsString, Step.class);
        assertThat(checkDigNeeded(mapper, step, ZeroCodeValueTokens.JSON_CONTENT), Is.is(false));


        ScenarioSpec scenarioSpec =
            smartUtils.scenarioFileToJava(
                "unit_test_files/json_content_unit_test/json_step_test_json_content.json", ScenarioSpec.class);
        step = scenarioSpec.getSteps().get(1);
        assertThat(checkDigNeeded(mapper, step, ZeroCodeValueTokens.JSON_CONTENT), Is.is(true));
    }

    @Test
    public void test_textNode() throws IOException {
        String jsonAsString = readJsonAsString("unit_test_files/filebody_unit_test/json_step_text_request.json");
        Step step = mapper.readValue(jsonAsString, Step.class);

        jsonPreProcessor.resolveJsonContent(step, new ScenarioExecutionState());
        String resultJsonStep = mapper.writeValueAsString(step);

        assertThat(read(resultJsonStep, "$.request"), Is.is("I am a simple text"));
    }


    protected StepExecutionState createStepWithRequestAndResponse(String stepName, String body) {
        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(TestUtility.createDummyStep(stepName));
        stepExecutionState.addRequest("{\n" +
                "    \"customer\": {\n" +
                "        \"firstName\": \"FIRST_NAME\"\n" +
                "    }\n" +
                "}");
        stepExecutionState.addResponse("{\n" +
                body +
                "}");
        return stepExecutionState;
    }
}
