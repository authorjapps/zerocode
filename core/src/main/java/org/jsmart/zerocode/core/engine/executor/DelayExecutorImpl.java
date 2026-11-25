package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class DelayExecutorImpl implements DelayExecutor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DelayExecutorImpl.class);

    private final ObjectMapper objectMapper;

    @Inject
    public DelayExecutorImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String execute(String url, String operationName, String requestJson) {
        long delayMillis = parseDelay(requestJson);

        LOGGER.info("Executing delay of {} ms", delayMillis);

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Delay execution was interrupted", e);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "Success");
        response.put("delayMillis", delayMillis);

        try {
            return prettyPrintJson(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize delay response", e);
        }
    }

    private long parseDelay(String requestJson) {
        if (requestJson == null || requestJson.trim().isEmpty()) {
            throw new IllegalArgumentException("Delay value must not be null or empty");
        }

        final long delayMillis;
        try {
            delayMillis = Long.parseLong(requestJson.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid delay value: " + requestJson, e);
        }

        if (delayMillis < 0) {
            throw new IllegalArgumentException("Delay must not be negative: " + delayMillis);
        }

        return delayMillis;
    }
}
