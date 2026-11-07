package org.jsmart.zerocode.core.engine.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayExecutorImpl implements DelayExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayExecutorImpl.java);

    @Override
    public String execute(String url, String operationName, String requestJson) {
        try {
            long delayMillis = parseDelay(url, operationName, requestJson);
            LOGGER.info("Executing delay of {} ms", delayMillis);
            Thread.sleep(delayMillis);
            return "{\"status\": \"DELAYED\", \"duration_ms\": " + delayMillis + "}";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Delay execution was interrupted", e);
        } catch (Exception e) {
            LOGGER.error("Error during delay execution: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private long parseDelay(String url, String operationName, String requestJson) {
        // Support delay specified in operationName, url, or requestJson
        try {
            if (operationName != null && operationName.matches("\\d+")) {
                return Long.parseLong(operationName);
            }
            if (url != null && url.contains(":")) {
                String[] parts = url.split(":");
                if (parts.length > 1 && parts[1].matches("\\d+")) {
                    return Long.parseLong(parts[1]);
                }
            }
            // Default 1000ms if not specified
            return 1000L;
        } catch (NumberFormatException e) {
            return 1000L;
        }
    }
}
