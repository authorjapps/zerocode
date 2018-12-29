package org.jsmart.zerocode.core.kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.jsmart.zerocode.core.kafka.send.message.Records;
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
        assertThat(json, is("{\"status\":\"Ok\",\"message\":\"test message\",\"recordCount\":10}"));

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
                "        },\n" +
                "        \"checksum\": 100\n" +
                "    }\n" +
                "}",
                jsonMsg, LENIENT);

    }

    @Test
    public void testProduceRecord_KV() {
        String json = "{\n" +
                "    \"key\": \"key1\",\n" +
                "    \"value\": {\n" +
                "        \"id\": \"201\"\n" +
                "    }\n" +
                "}";
        ProducerRecord producerRecord = gson.fromJson(json, ProducerRecord.class);
        assertThat(producerRecord.key(), is("key1"));
        String jsonBack = gson.toJson(producerRecord);
        JSONAssert.assertEquals(json, jsonBack, LENIENT);

        producerRecord = new ProducerRecord("topic2", 124, "Hello");
        jsonBack = gson.toJson(producerRecord);
        JSONAssert.assertEquals("{\"topic\":\"topic2\",\"key\":124,\"value\":\"Hello\"}", jsonBack, LENIENT);

        producerRecord = new ProducerRecord("topic2", 125L, "Hello");
        jsonBack = gson.toJson(producerRecord);
        JSONAssert.assertEquals("{\"topic\":\"topic2\",\"key\":125,\"value\":\"Hello\"}", jsonBack, LENIENT);

        producerRecord = new ProducerRecord("topic2", "key-123", "Hello");
        jsonBack = gson.toJson(producerRecord);
        JSONAssert.assertEquals("{\"topic\":\"topic2\",\"key\":\"key-123\",\"value\":\"Hello\"}", jsonBack, LENIENT);

    }

    @Test
    public void test_ProducerRecords() {
        final String json = "{\n" +
                "    \"records\": [\n" +
                "        {\n" +
                "            \"key\": 101,\n" +
                "            \"value\": \"value1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"key\": 102.0,\n" +
                "            \"value\": \"value2\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        Records producerRecords = gson.fromJson(json, Records.class);
        assertThat(producerRecords.getRecords().size(), is(2));
        assertThat(producerRecords.getRecords().get(0).key(), is(101.0)); //<-- convertes to double. But no harm

        String jsonBack = gson.toJson(producerRecords);
        JSONAssert.assertEquals("{\n" +
                "    \"records\": [\n" +
                "        {\n" +
                "            \"key\": 101.0,\n" + //<----------- Fails if 101 => Bug in skyscreamer
                "            \"value\": \"value1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"key\": 102.0,\n" +
                "            \"value\": \"value2\"\n" +
                "        }\n" +
                "    ]\n" +
                "}", jsonBack, LENIENT);

        jsonBack = gson.toJson(producerRecords.getRecords().get(0));
        JSONAssert.assertEquals("        {\n" +
                "            \"key\": 101,\n" + //<----------- Green even if 101, not 101.0 => Bug in skyscreamer
                "            \"value\": \"value1\"\n" +
                "        },\n",
                jsonBack, LENIENT);

        jsonBack = gson.toJson(producerRecords.getRecords().get(1));
        JSONAssert.assertEquals("        {\n" +
                        "            \"key\": 102.0,\n" +
                        "            \"value\": \"value2\"\n" +
                        "        }\n",
                jsonBack, LENIENT);
    }
}
