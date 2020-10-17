package org.jsmart.zerocode.integrationtests.customassert;

import org.jsmart.zerocode.core.engine.executor.javaapi.CustomAsserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * implement CustomAsserter interface method asserted(...)
 * or
 * implement your own flavoured method and pass it as below to the DSL:
 * e.g.
 * 1)
 * DSL: "key": "$CUSTOM.ASSERT:org.pkg...MyCustomComparator#myMethod:{\"expectDate\":\"2020-09-20\"}",
 * Java: public Boolean dateBetween(Map<String, Object> inputParamMap, Object actualDate) {...}
 * <p>
 * 2)
 * DSL: "actualDate": "$CUSTOM.ASSERT:org.pkg...MyCustomComparator#dateBetween:{\"fromDate\":\"2020-09-20\", \"toDate\":\"2021-09-20\"}",
 * Java: public Boolean dateBetween(Map<String, Object> inputParamMap, Object actualDate) {...}
 */
public class MyCustomComparator implements CustomAsserter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomComparator.class);

    // ---------------------------
    // "public static" also good
    // ---------------------------
    public Boolean asserted(Map<String, Object> inputParamMap, Object actualFieldValue) {
        int expectLen = Integer.parseInt(inputParamMap.get("expectLen").toString());
        return expectLen == actualFieldValue.toString().length();
    }

    public Boolean assertLength(Map<String, Object> inputParamMap, Object actualFieldValue) {
        int expectLen = Integer.parseInt(inputParamMap.get("expectLen").toString());
        int actualLength = actualFieldValue.toString().length();
        boolean success = (expectLen == actualLength);
        if (!success) {
            LOGGER.error("\n" +
                            "\t|\n" +
                            "\t|\n" +
                            "\t+---assertion error --> Actual length of {}}={} did not match expected length of {} \n" +
                            "\t|\n" +
                            "\t|\n",
                    actualFieldValue, actualLength, expectLen);
        }
        return success;
    }

    public static Integer strLen(String input) {
        return 6;
    }

    public static Integer strLen(String input1, String inut2) {
        return 7;
    }

}
