package org.jsmart.zerocode.core.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;

import javax.inject.Singleton;


public class ObjectMapperModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
    }
}



/*
 * Another way : By extending from AbstractModule, "binder" is still available via binder()
 */

//public class ObjectMapperModule extends AbstractModule {
//
//    @Override
//    public void configure() {
//        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
//    }
//}
