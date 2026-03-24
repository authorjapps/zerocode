package org.jsmart.zerocode.core.httpclient;

import java.util.Collections;
import java.util.Map;

/**
 * Lightweight HTTP response wrapper used internally by zerocode.
 * Replaces the former dependency on javax.ws.rs.core.Response (RESTEasy).
 *
 * Consumers who override BasicHttpClient#execute() should return this type.
 */
public class HttpResponse {

    private final int status;
    private final Map<String, Object> headers;
    private final String body;

    public HttpResponse(int status, Map<String, Object> headers, String body) {
        this.status = status;
        this.headers = headers != null ? headers : Collections.emptyMap();
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpResponse{status=" + status + ", headers=" + headers + ", body='" + body + "'}";
    }
}
