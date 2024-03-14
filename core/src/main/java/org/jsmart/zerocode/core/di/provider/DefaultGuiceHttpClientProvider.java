package org.jsmart.zerocode.core.di.provider;

import jakarta.inject.Provider;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;

public class DefaultGuiceHttpClientProvider implements Provider<BasicHttpClient> {

    @Override
    public BasicHttpClient get() {

        BasicHttpClient client = new SslTrustHttpClient();

        return client;
    }

}