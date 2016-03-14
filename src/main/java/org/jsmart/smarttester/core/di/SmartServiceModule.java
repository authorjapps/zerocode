package org.jsmart.smarttester.core.di;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import javax.inject.Singleton;

public class SmartServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        //binder.bind(ObjectMapper.class).to(ObjectMapperProvider.class).in(Singleton.class);
        //Names.bindProperties(binder, PropertiesProvider.getProperties());
        //binder.bind(ObjectMapper.class).to(ObjectMapper.class);
    }

    @Provides
    @Singleton
    public ObjectMapper getSingletonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        return objectMapper;

        /*
        ObjectMapper objectMapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );

        objectMapper.registerModule(new Jdk8Module());

        objectMapper.enableDefaultTypingAsProperty(
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT,
                JsonTypeInfo.Id.CLASS.getDefaultPropertyName()
        );


        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        */

    }
}
