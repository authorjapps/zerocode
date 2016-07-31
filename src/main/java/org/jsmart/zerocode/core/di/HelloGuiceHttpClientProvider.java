package org.jsmart.zerocode.core.di;

import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.RestEasyDefaultHttpClient;

import javax.inject.Provider;

public class HelloGuiceHttpClientProvider implements Provider<BasicHttpClient> {

    @Override
    public BasicHttpClient get() {

        BasicHttpClient client = new RestEasyDefaultHttpClient();

        return client;
    }

}