package org.jsmart.zerocode.core.kafka.send.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.CoreMatchers;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ProducerJsonRecordsTest {

    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testDeser_singleJsonRecord() throws IOException {
        String json = "{\n" +
                "                        \"value\": {\n" +
                "                            \"name\": \"Nicola\"\n" +
                "                        }\n" +
                "                    }";

        ProducerJsonRecord jsonRecord = objectMapper.readValue(json, ProducerJsonRecord.class);
        assertThat(jsonRecord.getValue().toString(), is("{\"name\":\"Nicola\"}"));
    }

    @Test
    public void testSerDe() throws IOException {
        final String json = "{\n" +
                "                \"recordType\" : \"JSON\",\n" +
                "                \"records\": [\n" +
                "                    {\n" +
                "                        \"key\": 101,\n" +
                "                        \"value\": {\n" +
                "                            \"name\" : \"Jey\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                ]\n" +
                "            }";

        Object recordType = JsonPath.read(json, "$.recordType");
        assertThat(recordType.toString(), is("JSON"));

        ProducerJsonRecords producerRawRecords = objectMapper.readValue(json, ProducerJsonRecords.class);
        assertThat(producerRawRecords.getRecords().size(), is(1));
        assertThat(producerRawRecords.getRecords().get(0).getKey(), is(101));
    }


    @Test
    public void testDeser_headers() throws JsonProcessingException {
        final String json = "{\n" +
                "                \"key\": 101,\n" +
                "                \"value\": {\n" +
                "                   \"name\" : \"Jey\"\n" +
                "                },\n" +
                "                \"headers\": {\n" +
                "                    \"key\": \"value\"\n" +
                "                }\n" +
                "            }";

        ProducerJsonRecord producerRawRecord = objectMapper.readValue(json, ProducerJsonRecord.class);
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("key", "value");
        assertThat(producerRawRecord.getHeaders(), CoreMatchers.equalTo(expectedHeaders));
    }
}