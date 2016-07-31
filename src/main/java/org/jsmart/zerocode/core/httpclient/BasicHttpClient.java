package org.jsmart.zerocode.core.httpclient;

import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.core.Response;
import java.util.Map;

public interface BasicHttpClient {
    /**
     *
     * @param httpUrl : path to end point
     * @param methodName : e.g. GET, PUT etc
     * @param headers : headers, cookies etc
     * @param queryParams : key-value query params after the '?' in the url
     * @param body : json body
     * @return : RestEasy http response consists of status code, entity, headers etc
     * @throws Exception
     */
    Response execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception;
}
