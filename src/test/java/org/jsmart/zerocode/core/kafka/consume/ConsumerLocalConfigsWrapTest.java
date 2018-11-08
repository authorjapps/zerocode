package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ConsumerLocalConfigsWrapTest {
    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSerDeser() throws IOException {
        ConsumerLocalConfigsWrap javaObject = new ConsumerLocalConfigsWrap(
                new ConsumerLocalConfigs("RAW:/target/ttt", "RAW", true, null, null));
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(javaObject);
        assertThat(json, is("{\"consumerLocalConfigs\":{\"fileDumpTo\":\"RAW:/target/ttt\",\"fileDumpType\":\"RAW\",\"commitAsync\":true}}"));

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testSerDeser_oneFieldOnly() throws IOException {
        ConsumerLocalConfigsWrap javaObject = new ConsumerLocalConfigsWrap(
                new ConsumerLocalConfigs("JSON:/target/ttt", "RAW", null, null, null));

        String json = objectMapper.writeValueAsString(javaObject);
        assertThat(json, is("{\"consumerLocalConfigs\":{\"fileDumpTo\":\"JSON:/target/ttt\",\"fileDumpType\":\"RAW\"}}"));

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testDser_json() throws IOException {
        String json = "{\n" +
                "                \"consumerLocalConfigs\": {\n" +
                "                    \"fileDumpTo\": \"target/temp/demo.txt\",\n" +
                "                    \"fileDumpType\": \"RAW\",\n" +
                "                    \"commitAsync\":true,\n" +
                "                    \"showRecordsAsResponse\":false\n" +
                "                }\n" +
                "\n" +
                "            }";

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);

        assertThat(javaPojo.getConsumerLocalConfigs().getFileDumpTo(), is("target/temp/demo.txt"));
        assertThat(javaPojo.getConsumerLocalConfigs().getShowRecordsAsResponse(), is(false));


    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void testDser_UnRecognizedField() throws IOException {
        String json = "{\n" +
                "                \"consumerLocalConfigs\": {\n" +
                "                    \"fileDumpTo\": \"target/temp/demo.txt\",\n" +
                "                    \"fileDumpType\": \"RAW\",\n" +
                "                    \"commitAsync\":true,\n" +
                "                    \"showRecordsAsResponse\":false,\n" +
                "                    \"MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS\": 5\n" +
                "                }\n" +
                "\n" +
                "            }";

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);

        assertThat(javaPojo.getConsumerLocalConfigs().getFileDumpTo(), is("target/temp/demo.txt"));
        assertThat(javaPojo.getConsumerLocalConfigs().getShowRecordsAsResponse(), is(false));


    }
}