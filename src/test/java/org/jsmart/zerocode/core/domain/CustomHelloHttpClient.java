package org.jsmart.zerocode.core.domain;

import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClient;

public class CustomHelloHttpClient implements HelloGuiceHttpClient {

    @Override
    public String printHello() {
        return "Custom http client----> SUCCESS";
    }
}