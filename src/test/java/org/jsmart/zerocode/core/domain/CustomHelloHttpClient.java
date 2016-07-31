package org.jsmart.zerocode.core.domain;

import org.jboss.resteasy.client.ClientResponse;
import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClient;

public class CustomHelloHttpClient implements HelloGuiceHttpClient {

    @Override
    public String printHello() {
        return "Custom http client----> SUCCESS";
    }

    @Override
    public ClientResponse execute(String httpUrl, String methodName, String requestJson) throws Exception {
        return null;
    }

}