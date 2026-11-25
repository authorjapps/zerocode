package org.jsmart.zerocode.core.utils;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java executor that pauses scenario execution for a requested number of
 * milliseconds.
 *
 * <p>Use it from a scenario step via the protocol mapping, e.g. in your
 * zerocode properties:
 * <pre>
 * java.api.protocol.mappings=util://DelayExecutor|org.jsmart.zerocode.core.utils.DelayExecutor
 * </pre>
 * and a step such as:
 * <pre>
 * {
 *   "name": "delay_3_sec",
 *   "url": "util://DelayExecutor",
 *   "method": "DELAY",
 *   "request": 3000,
 *   "assertions": {
 *     "status": "Success"
 *   }
 * }
 * </pre>
 */
public class DelayExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayExecutor.class);

    /**
     * Pauses execution for the requested number of milliseconds and returns
     * a result containing the status and the requested delay.
     *
     * @param millis the number of milliseconds to pause; must not be null
     *               and must not be negative
     * @return a map with {@code status} {@code "Success"} and the requested
     *         {@code delayMillis}
     * @throws IllegalArgumentException if {@code millis} is null or negative
     * @throws InterruptedException     if the sleeping thread is interrupted;
     *                                  the interrupt status is restored
     *                                  before throwing
     */
    public static Map<String, Object> DELAY(Long millis) throws InterruptedException {
        if (millis == null) {
            throw new IllegalArgumentException("Delay millis must not be null");
        }
        if (millis < 0) {
            throw new IllegalArgumentException("Delay millis must not be negative: " + millis);
        }

        LOGGER.debug("Delaying execution for {} ms", millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
        return Map.of("status", "Success", "delayMillis", millis);
    }
}
