package org.jsmart.zerocode.core.di.module;

import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Module;
import jakarta.inject.Singleton;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;


public class GsonModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Gson.class).toProvider(GsonSerDeProvider.class).in(Singleton.class);
    }
}

