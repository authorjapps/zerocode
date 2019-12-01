package org.jsmart.zerocode.zerocodejavaexec.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OrderTest {

    @Test
    public void testOrderSerDeser() throws IOException {
        Order order = new Order(1001, "brocoli", 20L);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(order);

        Order orderJava = objectMapper.readValue(json, Order.class);
        assertThat(orderJava.getItemName(), is("brocoli"));
        assertThat(orderJava.getOrderId(), is(1001));

        orderJava = objectMapper.readValue("{\"itemName\":\"brocoli\",\"quantity\":50}", Order.class);
        assertThat(orderJava.getQuantity(), is(50L));
        assertTrue(orderJava.getOrderId() == null);

    }
}