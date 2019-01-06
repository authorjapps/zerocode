package org.jsmart.zerocode.core.kafka.send.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

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

}