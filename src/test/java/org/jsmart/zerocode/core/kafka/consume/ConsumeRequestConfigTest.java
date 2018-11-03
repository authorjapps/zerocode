package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConsumeRequestConfigTest {
    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSerDeser() throws IOException {
        ConsumeRequestConfig javaObject = new ConsumeRequestConfig(
                new ConsumeTestProperties("RAW:/target/ttt", "RAW", true, null, null));
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(javaObject);
        assertThat(json, is("{\"consumeTestProperties\":{\"fileDumpTo\":\"RAW:/target/ttt\",\"fileDumpType\":\"RAW\",\"commitAsync\":true}}"));

        ConsumeRequestConfig javaPojo = objectMapper.readValue(json, ConsumeRequestConfig.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testSerDeser_oneFieldOnly() throws IOException {
        ConsumeRequestConfig javaObject = new ConsumeRequestConfig(
                new ConsumeTestProperties("JSON:/target/ttt", "RAW", null, null, null));

        String json = objectMapper.writeValueAsString(javaObject);
        assertThat(json, is("{\"consumeTestProperties\":{\"fileDumpTo\":\"JSON:/target/ttt\",\"fileDumpType\":\"RAW\"}}"));

        ConsumeRequestConfig javaPojo = objectMapper.readValue(json, ConsumeRequestConfig.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testDser_json() throws IOException {
        String json = "{\n" +
                "                \"consumeTestProperties\": {\n" +
                "                    \"fileDumpTo\": \"target/temp/demo.txt\",\n" +
                "                    \"fileDumpType\": \"RAW\",\n" +
                "                    \"commitAsync\":true,\n" +
                "                    \"showRecordsAsResponse\":false,\n" +
                "                    \"MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS\": 5\n" +
                "                }\n" +
                "\n" +
                "            }";

        ConsumeRequestConfig javaPojo = objectMapper.readValue(json, ConsumeRequestConfig.class);

        assertThat(javaPojo.getConsumeTestProperties().getFileDumpTo(), is("target/temp/demo.txt"));
        assertThat(javaPojo.getConsumeTestProperties().getShowRecordsAsResponse(), is(false));


    }
}