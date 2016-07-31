package org.jsmart.zerocode.core.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;

import javax.inject.Singleton;

public class HttpClientModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(BasicHttpClient.class).toProvider(HelloGuiceHttpClientProvider.class).in(Singleton.class);
    }
}