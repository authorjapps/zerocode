package org.jsmart.zerocode.core.kafka.send.message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.lang.reflect.Type;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class ProducerRawRecordsTest {
    final Gson gson = new GsonSerDeProvider().get();

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

        producerRecord = new ProducerRecord("topic2", "key-123", "{\"name\": \"Nicola\"}");
        jsonBack = gson.toJson(producerRecord);
        assertThat(producerRecord.value(), is("{\"name\": \"Nicola\"}"));
        JSONAssert.assertEquals("{\"topic\":\"topic2\",\"key\":\"key-123\",\"value\":\"{\\\"name\\\": \\\"Nicola\\\"}\"}", jsonBack, LENIENT);
    }

    @Test
    public void testDeser_headers() {
        Headers headers = new RecordHeaders();
        headers.add("headerKey1", "headerValue1".getBytes());
        headers.add("headerKey2", "headerValue2".getBytes());
        ProducerRecord producerRecord  = new ProducerRecord("topic2", null, "key-123", "Hello", headers);
        String jsonBack = gson.toJson(producerRecord);
        JSONAssert.assertEquals("{\"topic\":\"topic2\",\"headers\":{\"headerKey1\":\"headerValue1\",\"headerKey2\":\"headerValue2\"},\"key\":\"key-123\",\"value\":\"Hello\"}", jsonBack, LENIENT);
    }

    @Test
    public void test_ProducerRecords() {
        final String json = "{\n" +
                "\"recordType\": \"RAW\"," +
                "\"async\": true," +
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

        Object recordType = JsonPath.read(json, "$.recordType");
        assertThat(recordType.toString(), is("RAW"));

        ProducerRawRecords producerProducerRawRecords = gson.fromJson(json, ProducerRawRecords.class);
        assertThat(producerProducerRawRecords.getRecords().size(), is(2));
        assertThat(producerProducerRawRecords.getRecords().get(0).key(), is(101.0)); //<-- convertes to double. But no harm
        assertThat(producerProducerRawRecords.getAsync(), is(true));

        String jsonBack = gson.toJson(producerProducerRawRecords);
        JSONAssert.assertEquals("{\n" +
                "    \"records\": [\n" +
                "        {\n" +
                "            \"key\": 101.0,\n" + //<----------- Fails if 101 => While gson.fromJson(..), makes the int to Double
                "            \"value\": \"value1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"key\": 102.0,\n" +
                "            \"value\": \"value2\"\n" +
                "        }\n" +
                "    ]\n" +
                "}", jsonBack, LENIENT);

        jsonBack = gson.toJson(producerProducerRawRecords.getRecords().get(0));
        JSONAssert.assertEquals("        {\n" +
                        "            \"key\": 101,\n" + //<----------- Green even if 101, not 101.0 => Bug in skyscreamer
                        "            \"value\": \"value1\"\n" +
                        "        },\n",
                jsonBack, LENIENT);

        jsonBack = gson.toJson(producerProducerRawRecords.getRecords().get(1));
        JSONAssert.assertEquals("        {\n" +
                        "            \"key\": 102.0,\n" +
                        "            \"value\": \"value2\"\n" +
                        "        }\n",
                jsonBack, LENIENT);
    }

    @Test
    public void test_ProducerRecordsNull() {
        final String json = "{\n" +
                "\"recordType\": \"RAW\"," +
                "\"file\": \"abc.txt\"," +
                "\"async\": true" +
                "}";

        Object recordType = JsonPath.read(json, "$.recordType");
        assertThat(recordType.toString(), is("RAW"));

        ProducerRawRecords rawRecords = gson.fromJson(json, ProducerRawRecords.class);
        assertThat(rawRecords.getFile(), is("abc.txt"));
        assertThat(rawRecords.getRecords().size(), is(0));
        assertThat(rawRecords.getAsync(), is(true));

        ProducerRecord record = new ProducerRecord("topic1", 1, 2);

        boolean added = rawRecords.getRecords().add(record);
        assertThat(added, is(true));
        assertThat(rawRecords.getRecords().size(), is(0));
        rawRecords.setRecords(Arrays.asList(record));
        assertThat(rawRecords.getRecords().size(), is(1));


        ProducerRawRecords rawRecords2 = new ProducerRawRecords(null, true, "RAW", "abc.txt");
        assertThat(rawRecords2.getRecords().size(), is(0));
        rawRecords2.getRecords().add(record);
        assertThat(rawRecords2.getRecords().size(), is(1));
    }

    public static Type getType(String typeName) {
        try {
            Class<?> clazz = Class.forName(typeName);
            TypeToken<?> typeToken = TypeToken.get(clazz);
            return typeToken.getType();
        } catch (ClassNotFoundException ce) {
            throw new IllegalArgumentException("Unsupported type: " + typeName + " - " + ce);
        }
    }

}