package org.jsmart.zerocode.core.engine.executor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jayway.jsonpath.JsonPath;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class JsonServiceExecutorTest {

    public static final int PORT = 9999;
    public static final String HOST_WITH_CONTEXT = "http://localhost:" + PORT;

    JsonServiceExecutorImpl jsonServiceExecutor;
    Injector injector;
    SmartUtils smartUtils;
    SimpleRestJsonSimulatorsMain simulator ;

    @Before
    public void setUpMockEndPointsEtc() throws Exception {
        /*
         * See: All simulated end points @: test/resources/simulators/test_purpose_end_points.json
         */
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();

        injector = Guice.createInjector(new ApplicationMainModule("config_hosts_test.properties"));
        smartUtils = injector.getInstance(SmartUtils.class);
        jsonServiceExecutor = injector.getInstance(JsonServiceExecutorImpl.class);
    }

    @After
    public void releaseResouces() throws Exception {
        simulator.stop();
    }

    @Test
    public void willResolvePlaceHolder() throws Exception {
        String jsonString = smartUtils.getJsonDocumentAsString("engine/request_respone_actual.json");
        Object aPathValue = JsonPath.read(jsonString, "$.createPerson.request.id");
        assertThat(aPathValue, is("10101"));

        aPathValue = JsonPath.read(jsonString, "$.createPerson.response.addresses.length()");
        assertThat(aPathValue, is(2));

        aPathValue = JsonPath.read(jsonString, "$.createPerson.response.names.length()");
        assertThat(aPathValue, is(3));

        aPathValue = JsonPath.read(jsonString, "$.createPerson.response.addresses[0].houseNo.length()");
        assertThat(aPathValue, nullValue());
    }

    @Test
    public void willExecuteARESTCallForA_Scenario() throws Exception {
        /*
         * End-point available: http://localhost:9998/home/bathroom/1
         */
        String responseString = jsonServiceExecutor.executeRESTService(HOST_WITH_CONTEXT + "/home/bathroom/1", "GET", "{}");
        assertThat(responseString, containsString("Shower"));
        JSONAssert.assertEquals("{\n" +
                "  \"status\": 200,\n" +
                "  \"body\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Shower\",\n" +
                "    \"availability\": true\n" +
                "  }\n" +
                "}", responseString, false);
    }

    @Test
    public void willExecuteARESTCallForA_GET_smart_json() throws Exception {
        /*
         * End-point available: http://localhost:9998/home/bathroom/1
         */
        String scenariosJsonAsString = SmartUtils.readJsonAsString("06_test_with_place_holders/02_REST_end_point_GET.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();

        final String responseString = jsonServiceExecutor.executeRESTService(HOST_WITH_CONTEXT + serviceName, methodName, requestJson);
        assertThat(responseString, containsString("Shower"));
        JSONAssert.assertEquals("{\n" +
                "    \"status\": 200,\n" +
                "    \"headers\": {\n" +
                "        \"Transfer-Encoding\": [\n" +
                "            [\n" +
                "                \"chunked\"\n" +
                "            ]\n" +
                "        ],\n" +
                "        \"Content-Type\": [\n" +
                "            [\n" +
                "                \"application/json\"\n" +
                "            ]\n" +
                "        ],\n" +
                "        \"Connection\": [\n" +
                "            [\n" +
                "                \"keep-alive\"\n" +
                "            ]\n" +
                "        ]\n" +
                "    },\n" +
                "    \"body\": {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"Shower\",\n" +
                "        \"availability\": true\n" +
                "    }\n" +
                "}", responseString, false);
    }

    @Test
    public void willExecuteARESTCallForA_POST() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("06_test_with_place_holders/03_REST_end_point_POST.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();

        final String responseString = jsonServiceExecutor.executeRESTService(HOST_WITH_CONTEXT + serviceName, methodName, requestJson);
        assertThat(responseString, containsString("201"));
    }
}