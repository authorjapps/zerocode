package org.jsmart.zerocode.core.di.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javax.inject.Provider;

public class YamlObjectMapperProvider implements Provider<ObjectMapper> {

    @Override
    public ObjectMapper get() {

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        return objectMapper;
    }

}
