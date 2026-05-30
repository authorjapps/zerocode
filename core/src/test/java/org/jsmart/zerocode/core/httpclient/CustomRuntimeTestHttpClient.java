package org.jsmart.zerocode.core.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.Collections;
import java.util.Map;

public class CustomRuntimeTestHttpClient extends BasicHttpClient {

    @Override
    public CloseableHttpClient createHttpClient() throws Exception {
        return null;
    }

    @Override
    public HttpResponse execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception {
        int status = 200;
        String responseBody = "{\n" +
                "  \"result\" : \"via custom http client\" \n" +
                "}";
        return new HttpResponse(status, Collections.singletonMap("key1", "value1"), responseBody);
    }

}