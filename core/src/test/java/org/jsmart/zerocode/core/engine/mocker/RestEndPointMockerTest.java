package org.jsmart.zerocode.core.engine.mocker;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.MockStep;
import org.jsmart.zerocode.core.domain.MockSteps;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithWireMock;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.getWireMockServer;

@RunWith(JukitoRunner.class)
public class RestEndPointMockerTest {

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
    private ObjectMapper objectMapper;

    @Rule
    public WireMockRule rule = new WireMockRule(9073);

    RestEndPointMocker restEndPointMocker;
    String jsonDocumentAsString;
    ScenarioSpec scenarioDeserialized;
    MockSteps mockSteps;

    @Before
    public void beforeMethod() throws Exception {
        restEndPointMocker = new RestEndPointMocker();

        WireMock.configureFor(9073);

        jsonDocumentAsString = smartUtils.getJsonDocumentAsString("integration_test_files/wiremock_integration/wiremock_end_point_json_body.json");
        scenarioDeserialized = objectMapper.readValue(jsonDocumentAsString, ScenarioSpec.class);
        mockSteps = smartUtils.getMapper().readValue(scenarioDeserialized.getSteps().get(0).getRequest().toString(), MockSteps.class);

    }

    @Test
    public void willDeserializeA_VanilaFlow() throws Exception {

        assertThat(scenarioDeserialized, notNullValue());
        assertThat(scenarioDeserialized.getSteps().size(), is(1));
        assertThat(scenarioDeserialized.getScenarioName(), containsString("create_mocks"));

        MockSteps mockSteps = smartUtils.getMapper().readValue(scenarioDeserialized.getSteps().get(0).getRequest().toString(), MockSteps.class);

        assertThat(mockSteps.getMocks().get(0).getName(), containsString("Mock the Get Person"));
        assertThat(mockSteps.getMocks().get(1).getName(), containsString("Mock the POST Person"));
        assertThat(mockSteps.getMocks().get(2).getName(), containsString("Mock the PATCH Person"));

        MockStep mockStepGET = mockSteps.getMocks().get(0);
        assertThat(mockStepGET.getOperation(), is("GET"));
        assertThat(mockStepGET.getResponse().get("status").asInt(), is(200));
        assertThat(mockStepGET.getResponse().get("status").intValue(), is(200));
        assertThat(mockStepGET.getResponse().get("status").toString(), is("200"));
        JSONAssert.assertEquals(mockSteps.getMocks().get(0).getResponse().get("body").toString(),
                "{\n" +
                        "                \"id\": \"p001\",\n" +
                        "                \"source\": {\n" +
                        "                  \"code\": \"GOOGLE.UK\"\n" +
                        "                }\n" +
                        "              }",

                true);

    }

    @Test
    public void willMockASimpleGetEndPoint() throws Exception {

        final MockStep mockStep = mockSteps.getMocks().get(0);
        String jsonBodyRequest = mockStep.getResponse().get("body").toString();

        WireMock.configureFor(9073);
        givenThat(get(urlEqualTo(mockStep.getUrl()))
                .willReturn(aResponse()
                        .withStatus(mockStep.getResponse().get("status").asInt())
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(jsonBodyRequest)));

        ApacheHttpClientExecutor httpClientExecutor = new ApacheHttpClientExecutor();
        ClientRequest clientExecutor = httpClientExecutor.createRequest("http://localhost:9073" + mockStep.getUrl());
        clientExecutor.setHttpMethod("GET");
        ClientResponse serverResponse = clientExecutor.execute();

        final String respBodyAsString = (String) serverResponse.getEntity(String.class);
        JSONAssert.assertEquals(jsonBodyRequest, respBodyAsString, true);

        System.out.println("### zerocode: \n" + respBodyAsString);

    }

    @Test
    public void willMockRequest_withAnyQueryParameters() throws Exception {

        int WIRE_MOCK_TEST_PORT = 9077;

        final MockStep mockGetRequest = mockSteps.getMocks().get(0);
        String respBody = mockGetRequest.getResponse().get("body").toString();

        createWithWireMock(mockSteps, WIRE_MOCK_TEST_PORT);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:" + WIRE_MOCK_TEST_PORT + mockGetRequest.getUrl() + "?param1=value1&param2=value2");
        request.addHeader("key", "key-007");
        request.addHeader("secret", "secret-007");
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);
        System.out.print(response);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        JSONAssert.assertEquals(respBody, responseBodyActual, true);

        Assert.assertEquals("Content-Type", response.getEntity().getContentType().getName());
        Assert.assertEquals("application/json", response.getEntity().getContentType().getValue());

        getWireMockServer().stop();
    }

    @Test
    public void willMockAPostRequest() throws Exception {

        final MockStep mockPost = mockSteps.getMocks().get(1);
        String jsonBodyResponse = mockPost.getResponse().get("body").toString();

        final String bodyJson = mockPost.getRequest().get("body").toString(); //"{ \"id\" : \"p002\" }";
        stubFor(post(urlEqualTo(mockPost.getUrl()))
                .withRequestBody(equalToJson(bodyJson))
                .willReturn(aResponse()
                        .withStatus(mockPost.getResponse().get("status").asInt())
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(jsonBodyResponse)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:9073" + mockPost.getUrl());
        request.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(bodyJson);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);

        assertThat(response.getStatusLine().getStatusCode(), is(201));
        JSONAssert.assertEquals(jsonBodyResponse, responseBodyActual, true);

    }

    @Test
    public void willMockAPATCHRequest() throws Exception {

        final MockStep mockPatch = mockSteps.getMocks().get(2);
        String jsonBodyResponse = mockPatch.getResponse().get("body").toString(); // { "id": "cust345", "message": "email updated"  }

        final String bodyJson = mockPatch.getRequest().get("body").toString(); // { "email": "new_email_to_update@gmail.com" }
        stubFor(patch(urlEqualTo(mockPatch.getUrl()))
                .withRequestBody(equalToJson(bodyJson))
                .willReturn(aResponse()
                        .withStatus(mockPatch.getResponse().get("status").asInt())
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(jsonBodyResponse)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPatch request = new HttpPatch("http://localhost:9073" + mockPatch.getUrl());
        request.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(bodyJson);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        JSONAssert.assertEquals(jsonBodyResponse, responseBodyActual, true);

    }

    @Test
    public void willMockADELETERequest() throws Exception {

        final MockStep mockDelete = mockSteps.getMocks().get(3);

        stubFor(delete(urlEqualTo(mockDelete.getUrl()))
                .willReturn(aResponse()
                        .withStatus(mockDelete.getResponse().get("status").asInt())
                        .withHeader("Content-Type", APPLICATION_JSON)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete("http://localhost:9073" + mockDelete.getUrl());
        request.addHeader("Content-Type", "application/json");
        HttpResponse response = httpClient.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), is(204));

    }

    @Test
    public void willMockRequest_jsonBody() throws Exception {

        int WIRE_MOCK_TEST_PORT = 9077;

        final MockStep mockPost = mockSteps.getMocks().get(1);
        final String reqBody = mockPost.getRequest().get("body").toString(); //"{ \"id\" : \"p002\" }";
        String respBody = mockPost.getResponse().get("body").toString();

        createWithWireMock(mockSteps, WIRE_MOCK_TEST_PORT);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:" + WIRE_MOCK_TEST_PORT + mockPost.getUrl());
        request.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(reqBody);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);

        assertThat(response.getStatusLine().getStatusCode(), is(201));
        JSONAssert.assertEquals(respBody, responseBodyActual, true);

        getWireMockServer().stop();
    }

    @Test
    public void willMockRequest_respond_with_contentType() throws Exception {

        int WIRE_MOCK_TEST_PORT = 9077;

        final MockStep mockGetRequest = mockSteps.getMocks().get(0);
        String respBody = mockGetRequest.getResponse().get("body").toString();

        createWithWireMock(mockSteps, WIRE_MOCK_TEST_PORT);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:" + WIRE_MOCK_TEST_PORT + mockGetRequest.getUrl());
        request.addHeader("key", "key-007");
        request.addHeader("secret", "secret-007");
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        JSONAssert.assertEquals(respBody, responseBodyActual, true);

        Assert.assertEquals("Content-Type", response.getEntity().getContentType().getName());
        Assert.assertEquals("application/json", response.getEntity().getContentType().getValue());

        getWireMockServer().stop();
    }

    // --------------------------------------------------------------
    // ISSUE-202 - https://github.com/authorjapps/zerocode/issues/202
    // - xmlBody for SOAP mocking
    // - Fixed by - arunvelusamyd
    // --------------------------------------------------------------
    @Test
    public void willMockRequest_xmlBody() throws Exception {
        int WIRE_MOCK_TEST_PORT = 9077;

        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("integration_test_files/wiremock_integration/wiremock_end_point_soap_xml_body.json");
        ScenarioSpec scenarioDeserialized = objectMapper.readValue(jsonDocumentAsString, ScenarioSpec.class);
        MockSteps mockSteps = smartUtils.getMapper().readValue(scenarioDeserialized.getSteps().get(0).getRequest().toString(), MockSteps.class);

        final MockStep mockPost = mockSteps.getMocks().get(0);

        createWithWireMock(mockSteps, WIRE_MOCK_TEST_PORT);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:" + WIRE_MOCK_TEST_PORT + mockPost.getUrl());
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(responseBodyActual, containsString("<SOAP-ENV:Envelope xmlns:SOAP-ENV"));
        assertThat(responseBodyActual, containsString("<ns2:description>It is a tablet computers designed, developed and marketed by Apple.</ns2:description>"));

        getWireMockServer().stop();
    }

    @Test
    public void willMockAGetRequestWith_headers() throws Exception {

        final MockStep mockGetStep = mockSteps.getMocks().get(0);
        final Map<String, Object> headersMap = mockGetStep.getHeadersMap();

        final MappingBuilder requestBuilder = get(urlEqualTo(mockGetStep.getUrl()));

        // read request headers and set to request builder
        if (headersMap.size() > 0) {
            for (Object key : headersMap.keySet()) {
                requestBuilder.withHeader((String) key, equalTo((String) headersMap.get(key)));
            }
        }

        String jsonBodyResponse = mockGetStep.getResponse().get("body").toString();

        stubFor(requestBuilder
                .willReturn(aResponse()
                        .withStatus(mockGetStep.getResponse().get("status").asInt())
                        //.withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(jsonBodyResponse)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:9073" + mockGetStep.getUrl());
        request.addHeader("key", "key-007");
        request.addHeader("secret", "secret-007");

        HttpResponse response = httpClient.execute(request);
        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        System.out.println("### response: \n" + responseBodyActual);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
        JSONAssert.assertEquals(jsonBodyResponse, responseBodyActual, true);

    }

    @Test
    public void willMockASoapEndPoint() throws Exception {

        WireMock.configureFor(9073);

        String soapRequest = smartUtils.getJsonDocumentAsString("unit_test_files/soap_stub/soap_request.xml");

        final MappingBuilder requestBuilder = post(urlEqualTo("/samples/testcomplete12/webservices/Service.asmx"));
        requestBuilder.withRequestBody(equalToXml(soapRequest));
        requestBuilder.withHeader("Content-Type", equalTo("application/soap+xml; charset=utf-8"));

        String soapResponseExpected = smartUtils.getJsonDocumentAsString("unit_test_files/soap_stub/soap_response.xml");
        stubFor(requestBuilder
                .willReturn(aResponse()
                        .withStatus(200)
                        //.withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(soapResponseExpected)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:9073" + "/samples/testcomplete12/webservices/Service.asmx");
        request.addHeader("Content-Type", "application/soap+xml; charset=utf-8");
        StringEntity entity = new StringEntity(soapRequest);
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        final String responseBodyActual = IOUtils.toString(response.getEntity().getContent(), "UTF-8");

        assertThat(responseBodyActual, is(soapResponseExpected));
    }

}