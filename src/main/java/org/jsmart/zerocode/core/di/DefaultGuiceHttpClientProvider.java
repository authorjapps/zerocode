package org.jsmart.zerocode.core.di;

import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;

import javax.inject.Provider;

public class DefaultGuiceHttpClientProvider implements Provider<BasicHttpClient> {

    @Override
    public BasicHttpClient get() {

        BasicHttpClient client = new SslTrustHttpClient();

        return client;
    }

}