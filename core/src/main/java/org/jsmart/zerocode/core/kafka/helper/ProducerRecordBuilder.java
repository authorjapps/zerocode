package org.jsmart.zerocode.core.kafka.helper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.Map;

public class ProducerRecordBuilder {
    private String topic;
    private Object key;
    private Object value;
    private Headers headers;

    private ProducerRecordBuilder() {}

    public static ProducerRecordBuilder from(String topic, Object key, Object value) {
        ProducerRecordBuilder producerRecordBuilder = new ProducerRecordBuilder();

        producerRecordBuilder.topic = topic;
        producerRecordBuilder.key = key;
        producerRecordBuilder.value = value;

        return producerRecordBuilder;
    }

    public ProducerRecordBuilder withHeaders(Map<String, String> headers) {
        if (headers != null) {
            this.headers = new RecordHeaders();
            headers.forEach((hKey, hValue) -> this.headers.add(hKey, hValue.getBytes()));
        }

        return this;
    }

    public ProducerRecord<Object, Object> build() {
        return new ProducerRecord<>(topic, null, null, key, value, headers);
    }
}
