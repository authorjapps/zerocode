package org.jsmart.smarttester.core.di;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.jsmart.smarttester.core.runner.DefaultSmartMultiStepsRunner;
import org.jsmart.smarttester.core.runner.MultiStepsRunner;
import org.jsmart.smarttester.core.utils.SmartUtils;

import javax.inject.Singleton;

public class SmartServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        //binder.bind(ObjectMapper.class).to(ObjectMapperProvider.class).in(Singleton.class);
        //Names.bindProperties(binder, PropertiesProvider.getProperties());
        //binder.bind(ObjectMapper.class).to(ObjectMapper.class);
        //binder.bind(Class.class).to(FlowExamplePackagePicker.class);
        binder.bind(MultiStepsRunner.class).to(DefaultSmartMultiStepsRunner.class);
        binder.bind(SmartUtils.class);
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

    /*@Provides
    @Singleton
    public SmartUtils getSingletomSmartUtils(ObjectMapper objectMapper) {
        SmartUtils smartUtils = new SmartUtils();
        smartUtils.setMapper(objectMapper);

        return smartUtils;
    }*/
}
