package org.jsmart.zerocode.core.engine.assertion;

import net.minidev.json.JSONArray;

public class ArraySizeAsserter implements JsonAsserter {
    private final String path;
    private final int expectedSize;

    public ArraySizeAsserter(String path, int size) {
        this.path = path;
        expectedSize = size;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object result) {
        if (result instanceof JSONArray) {

            final JSONArray actualArrayValue = (JSONArray) result;

            if (actualArrayValue.size() == this.expectedSize) {

                return AssertionReport.createFieldMatchesReport();
            }

            return AssertionReport.createFieldDoesNotMatchReport(path, String.format("Array of size %d", expectedSize), result);

        } else {

            return AssertionReport.createFieldDoesNotMatchReport(path, "[]", result);

        }
    }

}
