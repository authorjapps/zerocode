package org.jsmart.zerocode.core.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClient;

public class RuntimeHttpClientModule implements Module {

    private final Class<? extends HelloGuiceHttpClient> customerHttpClientClazz;

    public RuntimeHttpClientModule(Class<? extends HelloGuiceHttpClient> customerHttpClientClazz) {
        this.customerHttpClientClazz = customerHttpClientClazz;
    }

    public void configure(Binder binder) {
        binder.bind(HelloGuiceHttpClient.class).to(customerHttpClientClazz);
    }
}