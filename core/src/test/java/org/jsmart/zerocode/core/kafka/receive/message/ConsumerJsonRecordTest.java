package org.jsmart.zerocode.core.kafka.receive.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConsumerJsonRecordTest {

    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSer() throws IOException {
        // TODO: Use assert iso sysout
        JsonNode key = objectMapper.readTree("\"key1\"");
        JsonNode value = objectMapper.readTree("\"val1\"");

        ConsumerJsonRecord record = new ConsumerJsonRecord(key, value, null);
        String json = objectMapper.writeValueAsString(record);
        System.out.println("1 json >> " + json);


        JsonNode key1 = objectMapper.readTree("123");

        record = new ConsumerJsonRecord(key1, value, null);
        json = objectMapper.writeValueAsString(record);
        System.out.println("1 json >> " + json);


        JsonNode key2 = objectMapper.readTree("23.45");

        record = new ConsumerJsonRecord(key2, value, null);
        json = objectMapper.writeValueAsString(record);
        System.out.println("2 json >> " + json);
    }

    @Test
    public void should_serialize_a_record_with_headers() throws JsonProcessingException {
        // given
        JsonNode key = objectMapper.readTree("123");
        JsonNode value = objectMapper.readTree("\"val\"");
        Map<String, String> headers = new HashMap<>();
        headers.put("hKey", "hValue");
        headers.put("hKeyWithNullValue", null);
        ConsumerJsonRecord record = new ConsumerJsonRecord(key, value, headers);

        // when
        String json = objectMapper.writeValueAsString(record);

        // then
        assertThat(json, CoreMatchers.equalTo("{\"key\":123,\"value\":\"val\",\"headers\":{\"hKey\":\"hValue\",\"hKeyWithNullValue\":null}}"));
    }

    @Test
    public void testDeser_singleJsonRecord() throws IOException {
        String json = "{\n" +
                "                        \"value\": {\n" +
                "                            \"name\": \"Nicola\"\n" +
                "                        }\n" +
                "                    }";

        ConsumerJsonRecord jsonRecord = objectMapper.readValue(json, ConsumerJsonRecord.class);
        assertThat(jsonRecord.getValue().toString(), is("{\"name\":\"Nicola\"}"));
    }
}
