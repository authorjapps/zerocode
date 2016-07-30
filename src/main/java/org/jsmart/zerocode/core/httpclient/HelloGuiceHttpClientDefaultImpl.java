package org.jsmart.zerocode.core.httpclient;

public class HelloGuiceHttpClientDefaultImpl implements HelloGuiceHttpClient{

    @Override
    public String printHello() {
        return "Default Hello";
    }
}
