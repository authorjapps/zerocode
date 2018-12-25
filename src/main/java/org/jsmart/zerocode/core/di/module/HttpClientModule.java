package org.jsmart.zerocode.core.di.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.di.provider.DefaultGuiceHttpClientProvider;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;

import javax.inject.Singleton;

public class HttpClientModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(BasicHttpClient.class).toProvider(DefaultGuiceHttpClientProvider.class).in(Singleton.class);
    }
}