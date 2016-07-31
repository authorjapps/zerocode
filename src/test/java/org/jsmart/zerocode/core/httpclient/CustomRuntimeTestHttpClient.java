package org.jsmart.zerocode.core.httpclient;

import javax.ws.rs.core.Response;
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


}