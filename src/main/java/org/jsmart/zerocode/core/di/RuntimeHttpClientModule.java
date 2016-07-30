package org.jsmart.zerocode.core.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClient;
import org.jsmart.zerocode.core.httpclient.HelloGuiceHttpClientRuntimeImpl;

public class RuntimeHttpClientModule implements Module {

    //private final HelloGuiceHttpClient customerHttpClient;
    private final Class<? extends HelloGuiceHttpClient> customerHttpClientClazz;

    public RuntimeHttpClientModule(Class<? extends HelloGuiceHttpClient> customerHttpClientClazz) {
        this.customerHttpClientClazz = customerHttpClientClazz;
    }

    public void configure(Binder binder) {
        //binder.bind(HelloGuiceHttpClient.class).to(HelloGuiceHttpClientRuntimeImpl.class);
        binder.bind(HelloGuiceHttpClient.class).to(customerHttpClientClazz);
    }
}