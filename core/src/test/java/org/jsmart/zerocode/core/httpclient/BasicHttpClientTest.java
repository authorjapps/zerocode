package org.jsmart.zerocode.core.httpclient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BasicHttpClientTest {
    private BasicHttpClient basicHttpClient;
    private Map<String, Object> header;

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
        Map<String, String> actualParams = Arrays.stream(nameValuePairString.split("&"))
            .map(entry -> entry.split("=", 2))
            .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
        
        assertThat(actualParams.get("Company"), is("Amazon"));
        assertThat(actualParams.get("age"), is("30"));
        assertThat(actualParams.get("worthInBillion"), is("999.999"));
    }

    @Test
    public void createRequestBuilder_spaceInKeyValue() throws IOException {
        header.put("Content-Type", "application/x-www-form-urlencoded");
        String reqBodyAsString = "{\"Name\":\"Larry Pg\",\"Company\":\"Amazon\",\"Title\":\"CEO\"}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        String nameValuePairString = EntityUtils.toString(requestBuilder.getEntity(), "UTF-8");
        Map<String, String> actualParams = Arrays.stream(nameValuePairString.split("&"))
            .map(entry -> entry.split("=", 2))
            .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));  
        assertThat(actualParams.get("Company"), is("Amazon"));
        assertThat(actualParams.get("Title"), is("CEO"));
        assertThat(actualParams.get("Name"), is("Larry+Pg"));
    }

    @Test
    public void createRequestBuilder_frontSlash() throws IOException {
        String reqBodyAsString = "{\"state/region\":\"singapore north\",\"Company\":\"Amazon\",\"Title\":\"CEO\"}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        String nameValuePairString = EntityUtils.toString(requestBuilder.getEntity(), "UTF-8");
        List<String> params = Arrays.asList(nameValuePairString.split("&"));
        Map<String, String> actualParams = Arrays.stream(nameValuePairString.split("&"))
            .map(entry -> entry.split("=", 2))
            .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));  
        assertThat(actualParams.get("Company"), is("Amazon"));
        assertThat(actualParams.get("Title"), is("CEO"));
        assertThat(actualParams.get("state%2Fregion"), is("singapore+north"));
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
        Map<String, String> actualParams = Arrays.stream(nameValuePairString.split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
            
        assertThat(actualParams.get("Company"), is("Amazon"));
        assertThat(actualParams.get("addresses"), is("%7Bcity%3DNewYork%2C+type%3DHeadOffice%7D"));
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

}