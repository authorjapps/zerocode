package org.jsmart.zerocode.core.kafka.receive.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNot;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ConsumerJsonRecordsTest {

    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testDeser_singleJsonRecord() throws IOException {
        String json = "{\n" +
                "                \"size\": 1,\n" +
                "                \"records\": [\n" +
                "                    {\n" +
                "                       \"key\": {\n" +
                "                            \"testkey\": \"keyvalue\"\n" +
                "                        },\n" +
                "                        \"value\": {\n" +
                "                            \"name\": \"Jey\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                ]\n" +
                "            }";

        ConsumerJsonRecords jsonRecords = objectMapper.readValue(json, ConsumerJsonRecords.class);
        assertThat(jsonRecords.getRecords().get(0).getValue().toString(), is("{\"name\":\"Jey\"}"));
        assertThat(jsonRecords.getRecords().get(0).getKey().get("testkey"), is(notNullValue()));
        assertThat(jsonRecords.getRecords().get(0).getKey().get("testkey").textValue(), is("keyvalue"));
        assertThat(jsonRecords.getSize(), is(1));
    }
}
