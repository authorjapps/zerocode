package org.jsmart.zerocode.core.httpclient;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

public class CustomRuntimeTestHttpClient implements BasicHttpClient {

    @Override
    public Response execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception {
        int status = 200;
        String responseBody = "{\n" +
                "  \"result\" : \"via custom http client\" \n" +
                "}";
        final Response built = Response.status(status).entity(responseBody).header("key1", "value1").build();

        return built;
    }

    @Override
    public String handleUrlAndQueryParams(String httpUrl, Map<String, Object> queryParams) {
        return null;
    }

    @Override
    public RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder) {
        return null;
    }

    @Override
    public String handleRequestBody(Object body) {
        return null;
    }

    @Override
    public Response handleResponse(CloseableHttpResponse httpResponse) throws IOException {
        return null;
    }


}