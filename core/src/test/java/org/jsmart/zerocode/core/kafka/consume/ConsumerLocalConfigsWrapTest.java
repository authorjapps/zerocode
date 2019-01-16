package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class ConsumerLocalConfigsWrapTest {
    ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSerDeser() throws IOException {
        ConsumerLocalConfigsWrap javaObject = new ConsumerLocalConfigsWrap(
                new ConsumerLocalConfigs("RAW",
                        "RAW:/target/ttt",
                        true,
                        null,
                        true,
                        3,
                        50L,
                        "1,0,test-topic"));
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(javaObject);
        assertEquals("{\n" +
                "   \"consumerLocalConfigs\": {\n" +
                "      \"fileDumpTo\": \"RAW:/target/ttt\",\n" +
                "      \"commitAsync\": true,\n" +
                "      \"maxNoOfRetryPollsOrTimeouts\": 3,\n" +
                "      \"pollingTime\": 50,\n" +
                "      \"seek\": \"1,0,test-topic\"\n" +
                "   }\n" +
                "}",
                json, LENIENT);

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testSerDeser_oneFieldOnly() throws IOException {
        ConsumerLocalConfigsWrap javaObject = new ConsumerLocalConfigsWrap(
                new ConsumerLocalConfigs("RAW", "JSON:/target/ttt",
                        null,
                        null,
                        false,
                        3,
                        null,
                        "1,0,test-topic"));

        String json = objectMapper.writeValueAsString(javaObject);
        assertEquals("{\n" +
                        "   \"consumerLocalConfigs\": {\n" +
                        "      \"fileDumpTo\": \"JSON:/target/ttt\",\n" +
                        "      \"showRecordsConsumed\": false,\n" +
                        "      \"maxNoOfRetryPollsOrTimeouts\": 3\n" +
                        "   }\n" +
                        "}",
                json, LENIENT);

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testDser_json() throws IOException {
        String json = "{\n" +
                "                \"consumerLocalConfigs\": {\n" +
                "                    \"fileDumpTo\": \"target/temp/demo.txt\",\n" +
                "                    \"commitAsync\":true,\n" +
                "                    \"showRecordsConsumed\":false\n" +
                "                }\n" +
                "\n" +
                "            }";

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);

        assertThat(javaPojo.getConsumerLocalConfigs().getFileDumpTo(), is("target/temp/demo.txt"));
        assertThat(javaPojo.getConsumerLocalConfigs().getShowRecordsConsumed(), is(false));


    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void testDser_UnRecognizedField() throws IOException {
        String json = "{\n" +
                "                \"consumerLocalConfigs\": {\n" +
                "                    \"fileDumpTo\": \"target/temp/demo.txt\",\n" +
                "                    \"commitAsync\":true,\n" +
                "                    \"showRecordsConsumed\":false,\n" +
                "                    \"MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS\": 5\n" +
                "                }\n" +
                "\n" +
                "            }";

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);

        assertThat(javaPojo.getConsumerLocalConfigs().getFileDumpTo(), is("target/temp/demo.txt"));
        assertThat(javaPojo.getConsumerLocalConfigs().getShowRecordsConsumed(), is(false));


    }
}