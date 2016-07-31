package org.jsmart.zerocode.core.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;

public class RuntimeHttpClientModule implements Module {

    private final Class<? extends BasicHttpClient> customerHttpClientClazz;

    public RuntimeHttpClientModule(Class<? extends BasicHttpClient> customerHttpClientClazz) {
        this.customerHttpClientClazz = customerHttpClientClazz;
    }

    public void configure(Binder binder) {
        binder.bind(BasicHttpClient.class).to(customerHttpClientClazz);
    }
}