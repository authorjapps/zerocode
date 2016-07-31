package org.jsmart.zerocode.core.httpclient;

import org.jboss.resteasy.client.ClientResponse;

public interface HelloGuiceHttpClient {
    String printHello();

    /**
     *
     * @param httpUrl : path to end point
     * @param methodName : e.g. GET, PUT etc
     * @param requestJson : headers, cookies, body etc
     * @return : A http response consists of status(e.g. 200, 500), entity, headers etc
     */
    ClientResponse execute(String httpUrl, String methodName, String requestJson) throws Exception;
}
