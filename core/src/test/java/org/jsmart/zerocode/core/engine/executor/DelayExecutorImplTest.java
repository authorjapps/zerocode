package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DelayExecutorImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DelayExecutorImpl delayExecutor = new DelayExecutorImpl(objectMapper);

    @Test
    public void shouldExecuteDelayAndReturnSuccess() throws Exception {
        String result = delayExecutor.execute("util://DelayExecutor", "DELAY", "100");

        JsonNode response = objectMapper.readTree(result);
        assertEquals("Success", response.get("status").asText());
        assertEquals(100L, response.get("delayMillis").asLong());
    }

    @Test
    public void shouldAcceptZeroDelay() throws Exception {
        String result = delayExecutor.execute("util://DelayExecutor", "DELAY", "0");

        JsonNode response = objectMapper.readTree(result);
        assertEquals("Success", response.get("status").asText());
        assertEquals(0L, response.get("delayMillis").asLong());
    }

    @Test
    public void shouldRejectNegativeDelay() {
        try {
            delayExecutor.execute("util://DelayExecutor", "DELAY", "-100");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("must not be negative"));
        }
    }

    @Test
    public void shouldRejectInvalidDelay() {
        try {
            delayExecutor.execute("util://DelayExecutor", "DELAY", "abc");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("Invalid delay value"));
        }
    }

    @Test
    public void shouldRejectMissingDelay() {
        try {
            delayExecutor.execute("util://DelayExecutor", "DELAY", null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("must not be null or empty"));
        }
    }

    @Test
    public void shouldRestoreInterruptFlagWhenExecutionIsInterrupted() {
        Thread.currentThread().interrupt();

        try {
            delayExecutor.execute("util://DelayExecutor", "DELAY", "1000");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage(), containsString("interrupted"));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }
}
