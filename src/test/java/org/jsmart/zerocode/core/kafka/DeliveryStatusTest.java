package org.jsmart.zerocode.core.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DeliveryStatusTest {

    @Test
    public void testSerDeser() throws IOException {
        DeliveryStatus deliveryStatus = new DeliveryStatus("Ok", "test message", 10);
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(deliveryStatus);
        assertThat(json, is("{\"status\":\"Ok\",\"message\":\"test message\",\"recordCount\":10}"));

        DeliveryStatus javaPojo = objectMapper.readValue(json, DeliveryStatus.class);
        assertThat(javaPojo, is(deliveryStatus));
    }

    @Test
    public void testSerDeser_statusOnly() throws IOException {
        DeliveryStatus deliveryStatus = new DeliveryStatus("Ok", null, null);
        ObjectMapper objectMapper = new ObjectMapperProvider().get();

        String json = objectMapper.writeValueAsString(deliveryStatus);
        assertThat(json, is("{\"status\":\"Ok\"}"));

        DeliveryStatus javaPojo = objectMapper.readValue(json, DeliveryStatus.class);
        assertThat(javaPojo, is(deliveryStatus));
    }
}