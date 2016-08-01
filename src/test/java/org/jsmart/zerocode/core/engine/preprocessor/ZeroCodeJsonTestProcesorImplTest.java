package org.jsmart.zerocode.core.engine.preprocessor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.jsonpath.JsonPath;
import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.engine.assertion.AssertionReport;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutorImpl;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class ZeroCodeJsonTestProcesorImplTest {
    public static final int PORT = 9998;
    public static final String HOST_WITH_CONTEXT = "http://localhost:" + PORT;

    JsonServiceExecutorImpl jsonServiceExecutor;
    Injector injector;
    SmartUtils smartUtils;
    SimpleRestJsonSimulatorsMain simulator ;

    ZeroCodeJsonTestProcesor jsonPreProcessor;

    @Before
    public void setUpStuff() throws Exception {
        injector = Guice.createInjector(new ApplicationMainModule("config_hosts_test.properties"));
        smartUtils = injector.getInstance(SmartUtils.class);
        jsonPreProcessor = new ZeroCodeJsonTestProcesorImpl(smartUtils.getMapper());

        /*
         * See: All simulated end points: test/resources/simulators/test_purpose_end_points.json
         */
        //simulator = new SimpleRestJsonSimulatorsMain(PORT);
        //simulator.start();

        //jsonServiceExecutor = new JsonServiceExecutorImpl();
        //jsonServiceExecutor.setHttpClientExecutor(new ApacheHttpClientExecutor());

    }

    @Test
    public void willResolveWithParamMap() throws Exception {
        ScenarioSpec scenarioSpec = smartUtils.jsonFileToJava("09_test_engine/01_request_with_place_holders.json", ScenarioSpec.class);
        final String requestJsonAsString = scenarioSpec.getSteps().get(0).getRequest().toString();

        final String resolvedRequestJson = jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);

        String lastName = JsonPath.read(resolvedRequestJson, "$.body.Customer.lastName");
        String nickName = JsonPath.read(resolvedRequestJson, "$.body.Customer.nickName");

        assertNotEquals(lastName, nickName);
    }

    @Test
    public void willCaptureAllPlaceHolders() throws Exception {
        ScenarioSpec scenarioSpec = smartUtils.jsonFileToJava("09_test_engine/01_request_with_place_holders.json", ScenarioSpec.class);
        final String requestJsonAsString = scenarioSpec.getSteps().get(0).getRequest().toString();

        final List<String> placeHolders = jsonPreProcessor.getAllTokens(requestJsonAsString);
        assertThat(placeHolders.size(), is(4));

        final String resolvedRequestJson = jsonPreProcessor.resolveStringJson(requestJsonAsString, requestJsonAsString);
        assertThat(resolvedRequestJson, containsString("\"staticName\":\"abcde\""));

        String specAsString = smartUtils.getJsonDocumentAsString("09_test_engine/01_request_with_place_holders.json");
        final String resolvedSpecString = jsonPreProcessor.resolveStringJson(specAsString, specAsString);
        assertThat(resolvedSpecString, containsString("\"url\": \"/persons/abc\""));
    }

    @Test
    public void willResolveJsonPathOfJayWay() throws Exception {
        String specAsString = smartUtils.getJsonDocumentAsString("09_test_engine/01_request_with_place_holders.json");

        final List<String> jsonPaths = jsonPreProcessor.getAllJsonPathTokens(specAsString);
        assertThat(jsonPaths.size(), is(2));

        final String resolvedSpecWithPaths = jsonPreProcessor.resolveStringJson(specAsString, specAsString);
        assertThat(resolvedSpecWithPaths, containsString("\"staticName\": \"abcde\""));

        //final String resolvedSpecResolvedPaths = jsonPreProcessor.resolveJsonPaths(resolvedSpecWithPaths);
        assertThat(resolvedSpecWithPaths, containsString("\"actualName\": \"${STATIC.ALPHABET:5}\""));
        assertThat(resolvedSpecWithPaths, containsString("\"actualNameSize\": \"2\""));
    }

    @Test
    public void willResolveJsonPathOfJayWayWith_SuppliedScenarioState() throws Exception {
        String specAsString = smartUtils.getJsonDocumentAsString("09_test_engine/02_1_two_requests_with_json_path_assertion.json");

        final List<String> jsonPaths = jsonPreProcessor.getAllJsonPathTokens(specAsString);
        assertThat(jsonPaths.size(), is(3));

        String scenarioState = "{\n" +
                "    \"step1\": {\n" +
                "        \"request\": {\n" +
                "            \"body\": {\n" +
                "                \"customer\": {\n" +
                "                    \"firstName\": \"FIRST_NAME\",\n" +
                "                    \"staticName\": \"ANOTHER_NAME\",\n" +
                "                    \"addresses\":[\"office-1\", \"home-2\"]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": {\n" +
                "            \"id\": 10101\n" +
                "        }\n" +
                "    }\n" +
                "}";
        final String resolvedSpecWithPaths = jsonPreProcessor.resolveStringJson(specAsString, scenarioState);
        assertThat(resolvedSpecWithPaths, containsString("\"staticName\": \"abcde\""));
        assertThat(resolvedSpecWithPaths, containsString("\"firstName\": \"FIRST_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"firstName2\": \"FIRST_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"actualName\": \"ANOTHER_NAME\""));
        assertThat(resolvedSpecWithPaths, containsString("\"noOfAddresses\": \"2\""));

    }

    @Test
    public void willResolveJsonPathOfJayWayFor_AssertionSection() throws Exception {
        ScenarioSpec scenarioSpec = smartUtils.jsonFileToJava("09_test_engine/02_1_two_requests_with_json_path_assertion.json", ScenarioSpec.class);

        // Get the 2nd step
        final String assertionsSectionAsString = scenarioSpec.getSteps().get(1).getAssertions().toString();
        String scenarioState = "{\n" +
                "    \"step1\": {\n" +
                "        \"request\": {\n" +
                "            \"body\": {\n" +
                "                \"customer\": {\n" +
                "                    \"firstName\": \"FIRST_NAME\",\n" +
                "                    \"staticName\": \"ANOTHER_NAME\",\n" +
                "                    \"addresses\":[\"office-1\", \"home-2\"]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": {\n" +
                "            \"id\": 10101\n" +
                "        }\n" +
                "    }\n" +
                "}";

        final String resolvedAssertions = jsonPreProcessor.resolveStringJson(assertionsSectionAsString, scenarioState);
        assertThat(resolvedAssertions, containsString("\"actualName\":\"ANOTHER_NAME\""));

        // start assertion
        String sapmleExecutionResult =
                smartUtils.getJsonDocumentAsString("09_test_engine/02_2_sample_resolved_execution_response.json");
        List<JsonAsserter> asserters = jsonPreProcessor.createAssertersFrom(resolvedAssertions);
        assertThat(asserters.size(), is(17));

        List<AssertionReport> failedReports = jsonPreProcessor.assertAllAndReturnFailed(asserters, sapmleExecutionResult);

        System.out.println("###failedReports : " + failedReports);
        assertThat(failedReports.toString(), containsString("did not match the expected value 'NOT NULL'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'ANOTHER_NAME'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'NULL'"));
        assertThat(failedReports.toString(), containsString("citizenship' with actual value '[{"));
        assertThat(failedReports.toString(), containsString("personalities' with actual value 'null'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value '[]'"));
        assertThat(failedReports.toString(), not(containsString("pastActivities")));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'Array of size 5'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'Array of size 4'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'containing sub-string:DaddyWithMac'"));
        assertThat(failedReports.toString(), containsString("did not match the expected value 'Greater Than:499'"));
        assertThat(failedReports.toString(), containsString("'null' did not match the expected value 'Greater Than:388'"));
        assertThat(failedReports.toString(), containsString("actual value '1400' did not match the expected value 'Lesser Than:1300'"));
        assertThat(failedReports.size(), is(11));

    }

    @Test
    public void willResolveTextNodeFor_Assertion() throws Exception {

        final String assertionsSectionTextNodeAsString = "\"id-generated-0101\"";

        String scenarioState = "{\n" +
                "    \"step1\": {\n" +
                "        \"request\": {\n" +
                "            \"body\": {\n" +
                "                \"customer\": {\n" +
                "                    \"firstName\": \"FIRST_NAME\",\n" +
                "                    \"staticName\": \"ANOTHER_NAME\",\n" +
                "                    \"addresses\":[\"office-1\", \"home-2\"]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": \"id-generated-0101-aga-baga\"" + //<--- In this case this is not relevant as this path is not used
                "    }\n" +
                "}";

        final String resolvedAssertions = jsonPreProcessor.resolveStringJson(assertionsSectionTextNodeAsString, scenarioState);
        assertThat(resolvedAssertions, containsString("\"id-generated-0101\""));

        // start assertion
        List<JsonAsserter> asserters = jsonPreProcessor.createAssertersFrom(resolvedAssertions);
        assertThat(asserters.size(), is(1));

        String sampleExecutionResult = "\"id-generated-0101-XY\"";
        List<AssertionReport> failedReports = jsonPreProcessor.assertAllAndReturnFailed(asserters, sampleExecutionResult);

        System.out.println("###failedReports : " + failedReports);
        assertThat(failedReports.toString(), containsString("'$' with actual value 'id-generated-0101-XY' did not match the expected value 'id-generated-0101'"));
        assertThat(failedReports.size(), is(1));
    }

    @Test
    public void willResolveIntegerNodeFor_Assertion() throws Exception {

        final Integer assertionsSectionInt = 1099;

        String scenarioState = "{\n" +
                "    \"step1\": {\n" +
                "        \"request\": {\n" +
                "            \"body\": {\n" +
                "                \"customer\": {\n" +
                "                    \"firstName\": \"FIRST_NAME\",\n" +
                "                    \"staticName\": \"ANOTHER_NAME\",\n" +
                "                    \"addresses\":[\"office-1\", \"home-2\"]\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": 300000" + //<--- In this case this is not relevant as this path is not used
                "    }\n" +
                "}";

        final String resolvedAssertions = jsonPreProcessor.resolveStringJson(assertionsSectionInt.toString(), scenarioState);
        assertThat(resolvedAssertions, containsString("1099"));

        // start assertion
        List<JsonAsserter> asserters = jsonPreProcessor.createAssertersFrom(resolvedAssertions);
        assertThat(asserters.size(), is(1));

        Integer sampleExecutionResult = 1077;
        List<AssertionReport> failedReports = jsonPreProcessor.assertAllAndReturnFailed(asserters, sampleExecutionResult.toString());

        System.out.println("###failedReports : " + failedReports);
        assertThat(failedReports.toString(), containsString("'$' with actual value '1077' did not match the expected value '1099'"));
        assertThat(failedReports.size(), is(1));
    }
}