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
    public void testSerDeser_seekEpoch() throws IOException {
        ConsumerLocalConfigsWrap javaObject = new ConsumerLocalConfigsWrap(
                new ConsumerLocalConfigs("RAW",
                        "RAW:/target/ttt",
                        true,
                        null,
                        true,
                        3,
                        50L,
                        false,
                        "$.JSON.Path",
                        null,
                        "1706940293669",
                        null));
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(javaObject);
        assertEquals("{\n" +
                        "    \"consumerLocalConfigs\":\n" +
                        "    {\n" +
                        "        \"recordType\": \"RAW\",\n" +
                        "        \"fileDumpTo\": \"RAW:/target/ttt\",\n" +
                        "        \"commitAsync\": true,\n" +
                        "        \"showRecordsConsumed\": true,\n" +
                        "        \"maxNoOfRetryPollsOrTimeouts\": 3,\n" +
                        "        \"pollingTime\": 50,\n" +
                        "        \"cacheByTopic\": false,\n" +
                        "        \"filterByJsonPath\": \"$.JSON.Path\",\n" +
                        "        \"seekEpoch\": \"1706940293669\"\n" +
                        "    }\n" +
                        "}",
                json, LENIENT);

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);
        assertThat(javaPojo, is(javaObject));
    }

    @Test
    public void testSerDeser_seekTimestamp() throws IOException {
        ConsumerLocalConfigsWrap javaObject = new ConsumerLocalConfigsWrap(
                new ConsumerLocalConfigs("RAW",
                        "RAW:/target/ttt",
                        true,
                        null,
                        true,
                        3,
                        50L,
                        false,
                        "$.JSON.Path",
                        null,
                        null,
                        new SeekTimestamp("2024-01-29T19:35:21.959340", "yyyy-MM-dd'T'HH:mm:ss.ssssss")));
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(javaObject);
        assertEquals("{\n" +
                        "    \"consumerLocalConfigs\":\n" +
                        "    {\n" +
                        "        \"recordType\": \"RAW\",\n" +
                        "        \"fileDumpTo\": \"RAW:/target/ttt\",\n" +
                        "        \"commitAsync\": true,\n" +
                        "        \"showRecordsConsumed\": true,\n" +
                        "        \"maxNoOfRetryPollsOrTimeouts\": 3,\n" +
                        "        \"pollingTime\": 50,\n" +
                        "        \"cacheByTopic\": false,\n" +
                        "        \"filterByJsonPath\": \"$.JSON.Path\",\n" +
                        "        \"seekTimestamp\":\n" +
                        "        {\n" +
                        "            \"timestamp\": \"2024-01-29T19:35:21.959340\",\n" +
                        "            \"format\": \"yyyy-MM-dd'T'HH:mm:ss.ssssss\"\n" +
                        "        }\n" +
                        "    }\n" +
                        "}",
                json, LENIENT);

        ConsumerLocalConfigsWrap javaPojo = objectMapper.readValue(json, ConsumerLocalConfigsWrap.class);
        assertThat(javaPojo, is(javaObject));
    }

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
                        false,
                        "$.JSON.Path",
                        "1,0,test-topic",
                        null, null));
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(javaObject);
        assertEquals("{\n" +
                "   \"consumerLocalConfigs\": {\n" +
                "      \"fileDumpTo\": \"RAW:/target/ttt\",\n" +
                "      \"commitAsync\": true,\n" +
                "      \"maxNoOfRetryPollsOrTimeouts\": 3,\n" +
                "      \"pollingTime\": 50,\n" +
                "      \"filterByJsonPath\": \"$.JSON.Path\",\n" +
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
                        false,
                        null,
                        "1,0,test-topic",
                        null, null));

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