package org.jsmart.zerocode.core.kafka.receive.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

public class ConsumerJsonRecordTest {

    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSer() throws IOException {
        String key = "key1";
        JsonNode value = objectMapper.readTree("\"val1\"");

        ConsumerJsonRecord record = new ConsumerJsonRecord(key, null, value);
        String json = objectMapper.writeValueAsString(record);
        System.out.println("1 json >> " + json);


        Integer key1 = 123;
        record = new ConsumerJsonRecord(key1, null, value);
        json = objectMapper.writeValueAsString(record);
        System.out.println("1 json >> " + json);


        Object key2 = 23.45;
        record = new ConsumerJsonRecord(key2, null, value);
        json = objectMapper.writeValueAsString(record);
        System.out.println("2 json >> " + json);

    }
}