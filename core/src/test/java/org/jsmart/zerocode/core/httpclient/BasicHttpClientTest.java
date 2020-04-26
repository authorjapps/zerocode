package org.jsmart.zerocode.core.httpclient;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class BasicHttpClientTest {
    private BasicHttpClient basicHttpClient;
    private Map<String, Object> header;

    @Rule
    public WireMockRule rule = new WireMockRule(9073);

    @Before
    public void setUp() {
        basicHttpClient = new BasicHttpClient();
        header = new HashMap<String, Object>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
    }

    @Test
    public void createRequestBuilder() throws IOException {
        header.put("Content-Type", "application/x-www-form-urlencoded");
        String reqBodyAsString = "{\"Company\":\"Amazon\",\"age\":30,\"worthInBillion\":999.999}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        String nameValuePairString = EntityUtils.toString(requestBuilder.getEntity(), "UTF-8");
        assertThat(requestBuilder.getMethod(), is("POST"));
        assertThat(nameValuePairString, is("Company=Amazon&worthInBillion=999.999&age=30"));
    }

    @Test
    public void createRequestBuilder_spaceInKeyValue() throws IOException {
        header.put("Content-Type", "application/x-www-form-urlencoded");
        String reqBodyAsString = "{\"Name\":\"Larry Pg\",\"Company\":\"Amazon\",\"Title\":\"CEO\"}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        String nameValuePairString = EntityUtils.toString(requestBuilder.getEntity(), "UTF-8");
        assertThat(nameValuePairString, is("Company=Amazon&Title=CEO&Name=Larry+Pg"));
    }

    @Test
    public void createRequestBuilder_frontSlash() throws IOException {
        String reqBodyAsString = "{\"state/region\":\"singapore north\",\"Company\":\"Amazon\",\"Title\":\"CEO\"}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        String nameValuePairString = EntityUtils.toString(requestBuilder.getEntity(), "UTF-8");
        assertThat(nameValuePairString, is("Company=Amazon&Title=CEO&state%2Fregion=singapore+north"));
    }

    @Test
    public void test_queryParamEncodedChar() throws URISyntaxException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("q1", "value1");
        queryParamsMap.put("q2", "value2");
        queryParamsMap.put("state/region", "London UK");
        String effectiveUrl = basicHttpClient.handleUrlAndQueryParams("http://abc.com", queryParamsMap);

        assertThat(effectiveUrl, is("http://abc.com?q1=value1&q2=value2&state%2Fregion=London+UK"));
    }

    @Test
    public void createRequestBuilder_jsonValue() throws IOException {
        header.put("Content-Type", "application/x-www-form-urlencoded");
        String reqBodyAsString = "{\n" +
                "  \"Company\": \"Amazon\",\n" +
                "  \"addresses\": {\n" +
                "    \"city\": \"NewYork\",\n" +
                "    \"type\": \"HeadOffice\"\n" +
                "  }\n" +
                "}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        String nameValuePairString = EntityUtils.toString(requestBuilder.getEntity(), "UTF-8");
        assertThat(requestBuilder.getMethod(), is("POST"));
        //On the server side: address={city=NewYork, type=HeadOffice}
        assertThat(nameValuePairString, is("Company=Amazon&addresses=%7Bcity%3DNewYork%2C+type%3DHeadOffice%7D"));
    }

    @Test
    public void test_emptyQueryParams() throws URISyntaxException {
        String effectiveUrl = basicHttpClient.handleUrlAndQueryParams("http://test-url", new HashMap<>());
        assertThat(effectiveUrl, is("http://test-url"));
        effectiveUrl = basicHttpClient.handleUrlAndQueryParams("http://test-url", null);
        assertThat(effectiveUrl, is("http://test-url"));
    }
    
    @Test
    public void test_multipart_without_file() throws IOException {
    	basicHttpClient.createFileUploadRequestBuilder("http://test-url", "POST", "{\"modelStorage\":\"DB\",\"sketchingAlgorithm\":\"UPDATE\"}");
    }

    @Test
    public void willMockUTF16Response() throws Exception {
        WireMock.configureFor(9073);
        givenThat(get(urlEqualTo("/charset/utf16"))
                .willReturn(aResponse()
                        .withStatus(200)
                        // ------------------------------------------------------------------------
                        // If you make the value  charset=UTF-8, the test will fail, that means
                        // When server is sending UTF-16 encoded chars, it should also set the
                        // charset to UTF-16 e.g. "application/json; charset=UTF-16", if not set
                        // by the server, the framework will default to UTF-8 and will not be able
                        // to interpret the chars as expected
                        // ------------------------------------------------------------------------
                        .withHeader("Content-Type", "application/json; charset=UTF-16")
                        // ------------------------------------------------------------------------
                        // "This is utf-16 text" is utf-16 encoded and converted to base64.
                        // Reference Link: https://www.base64encode.org/
                        // ------------------------------------------------------------------------
                        .withBase64Body("//5UAGgAaQBzACAAaQBzACAAdQB0AGYALQAxADYAIAB0AGUAeAB0AA==")));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:9073" + "/charset/utf16");

        CloseableHttpResponse response = httpClient.execute(request);

        BasicHttpClient basicHttpClient = new BasicHttpClient();
        String responseBodyActual = (String) basicHttpClient.handleResponse(response).getEntity();
        assertThat(responseBodyActual, CoreMatchers.is("This is utf-16 text"));

        // --------------------------------------
        // Now set to UTF-8 and see the assertion
        // --------------------------------------
        givenThat(get(urlEqualTo("/charset/utf16"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBase64Body("//5UAGgAaQBzACAAaQBzACAAdQB0AGYALQAxADYAIAB0AGUAeAB0AA==")));
        response = httpClient.execute(request);
        responseBodyActual = (String) basicHttpClient.handleResponse(response).getEntity();
        assertThat(responseBodyActual, not("This is utf-16 text"));
    }

    @Test
    public void willMockUTF8Response() throws Exception {

        final String response = "utf-8 encoded text";
        WireMock.configureFor(9073);
        givenThat(get(urlEqualTo("/charset/utf8"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody(response)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:9073" + "/charset/utf8");

        CloseableHttpResponse closeableHttpResponse = httpClient.execute(request);

        BasicHttpClient basicHttpClient = new BasicHttpClient();
        final String responseBodyActual = (String) basicHttpClient.handleResponse(closeableHttpResponse).getEntity();
        assertThat(responseBodyActual, CoreMatchers.is(response));
    }

    @Test
    public void willMockDefaultResponseEncoding() throws Exception {

        final String response = "utf-8 encoded text";
        WireMock.configureFor(9073);
        givenThat(get(urlEqualTo("/charset/none"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(response)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:9073" + "/charset/none");

        CloseableHttpResponse closeableHttpResponse = httpClient.execute(request);

        BasicHttpClient basicHttpClient = new BasicHttpClient();
        final String responseBodyActual = (String) basicHttpClient.handleResponse(closeableHttpResponse).getEntity();
        assertThat(responseBodyActual, CoreMatchers.is(response));
    }
}