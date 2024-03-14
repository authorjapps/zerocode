package org.jsmart.zerocode.core.di.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import jakarta.inject.Singleton;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.di.provider.YamlObjectMapperProvider;

public class ObjectMapperModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        binder.bind(ObjectMapper.class)
                .annotatedWith(Names.named("YamlMapper"))
                .toProvider(YamlObjectMapperProvider.class).in(Singleton.class);
    }
}

