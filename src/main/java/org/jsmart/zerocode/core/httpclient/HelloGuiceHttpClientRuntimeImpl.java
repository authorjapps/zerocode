package org.jsmart.zerocode.core.httpclient;

import org.jboss.resteasy.client.ClientResponse;

public class HelloGuiceHttpClientRuntimeImpl implements HelloGuiceHttpClient{

    @Override
    public String printHello() {
        return "---> Runtime Hello";
    }

    @Override
    public ClientResponse execute(String httpUrl, String methodName, String requestJson) throws Exception {
        return null;
    }

}
