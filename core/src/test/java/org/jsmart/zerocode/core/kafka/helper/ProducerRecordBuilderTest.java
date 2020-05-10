package org.jsmart.zerocode.core.kafka.helper;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ProducerRecordBuilderTest {

    @Test
    public void should_build_producer_record_without_headers() {
        ProducerRecord<Object, Object> producerRecord = ProducerRecordBuilder
                .from("topic", "key", "value")
                .build();

        assertMandatoryProperties(producerRecord);
    }

    @Test
    public void should_build_producer_record_with_headers() {
        ProducerRecord<Object, Object> producerRecord = ProducerRecordBuilder
                .from("topic", "key", "value")
                .withHeaders(Collections.singletonMap("headerKey", "headerValue"))
                .build();

        assertMandatoryProperties(producerRecord);
        Header[] headers = producerRecord.headers().toArray();
        assertEquals(1, headers.length);
        assertEquals("headerKey", headers[0].key());
        assertThat("headerValue".getBytes(), CoreMatchers.equalTo(headers[0].value()));
    }

    private void assertMandatoryProperties(ProducerRecord<Object, Object> producerRecord) {
        assertEquals("topic", producerRecord.topic());
        assertEquals("key", producerRecord.key());
        assertEquals("value", producerRecord.value());
    }
}
