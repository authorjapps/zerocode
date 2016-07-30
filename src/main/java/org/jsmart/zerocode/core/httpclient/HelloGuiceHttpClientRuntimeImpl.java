package org.jsmart.zerocode.core.httpclient;

public class HelloGuiceHttpClientRuntimeImpl implements HelloGuiceHttpClient{

    @Override
    public String printHello() {
        return "---> Runtime Hello";
    }
}
