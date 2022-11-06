package org.jsmart.zerocode.core.kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class DeliveryDetailsTest {
    // gson used for serialization purpose-only in the main code.
    // In the tests it's used for both, ser-deser, but
    // the framework never deserializes the delivery details
    // in reality. Even if so, it works for both as tested below.
    final Gson gson = new GsonSerDeProvider().get();

    @Test
    public void testSerDeser() throws IOException {
        DeliveryDetails deliveryDetails = new DeliveryDetails("Ok", "test message", 10, null);

        String json = gson.toJson(deliveryDetails);
        assertThat(json, is("{\"status\":\"Ok\",\"message\":\"test message\",\"size\":10}"));

        DeliveryDetails javaPojo = gson.fromJson(json, DeliveryDetails.class);
        assertThat(javaPojo, is(deliveryDetails));
    }

    @Test
    public void testSerDeser_statusOnly() throws IOException {
        DeliveryDetails deliveryDetails = new DeliveryDetails("Ok", null, null, null);

        String json = gson.toJson(deliveryDetails);
        assertThat(json, is("{\"status\":\"Ok\"}"));

        DeliveryDetails javaPojo = gson.fromJson(json, DeliveryDetails.class);
        assertThat(javaPojo, is(deliveryDetails));
    }

    @Test
    public void testSerViaGson() {
        DeliveryDetails deliveryDetails = new DeliveryDetails("Ok", null, null, null);
        String jsonMsg = gson.toJson(deliveryDetails);
        assertThat(jsonMsg, is("{\"status\":\"Ok\"}"));

        TopicPartition topicPartition = new TopicPartition("test-topic", 0);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition,
                0,
                2,
                1546008192846L,
                100L,
                1,
                45);
        deliveryDetails = new DeliveryDetails("Ok", null, null, recordMetadata);
        jsonMsg = gson.toJson(deliveryDetails);

        JSONAssert.assertEquals("{\n" +
                        "    \"status\": \"Ok\",\n" +
                        "    \"recordMetadata\": {\n" +
                        "        \"offset\": 2,\n" +
                        "        \"timestamp\": 1546008192846,\n" +
                        "        \"serializedKeySize\": 1,\n" +
                        "        \"serializedValueSize\": 45,\n" +
                        "        \"topicPartition\": {\n" +
                        "            \"hash\": 0,\n" +
                        "            \"partition\": 0,\n" +
                        "            \"topic\": \"test-topic\"\n" +
                        "        }\n" +
                        "    }\n" +
                        "}",
                jsonMsg, LENIENT);

    }
}