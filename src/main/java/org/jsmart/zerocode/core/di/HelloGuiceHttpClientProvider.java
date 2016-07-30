package org.jsmart.zerocode.core.di;

import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClient;
import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClientDefaultImpl;

import javax.inject.Provider;

public class HelloGuiceHttpClientProvider implements Provider<HelloGuiceHttpClient> {

    @Override
    public HelloGuiceHttpClient get() {

        HelloGuiceHttpClient client = new HelloGuiceHttpClientDefaultImpl();

        return client;
    }

}