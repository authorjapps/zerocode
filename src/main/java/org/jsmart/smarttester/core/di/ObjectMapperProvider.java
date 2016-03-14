//package org.jsmart.smarttester.core.di;
//
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//
//import javax.inject.Provider;
//
//
//public class ObjectMapperProvider implements Provider<ObjectMapper> {
//
//    @Override
//    public ObjectMapper get() {
//        ObjectMapper objectMapper = new ObjectMapper().configure(
//                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//                false);
//        objectMapper.enableDefaultTypingAsProperty(
//                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT,
//                JsonTypeInfo.Id.CLASS.getDefaultPropertyName());
//        objectMapper.registerModule(new Jdk8Module());
//        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
//
//        return objectMapper;
//    }
//
//}
