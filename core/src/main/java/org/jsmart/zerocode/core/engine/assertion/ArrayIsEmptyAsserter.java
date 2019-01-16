package org.jsmart.zerocode.core.engine.assertion;

import net.minidev.json.JSONArray;

public class ArrayIsEmptyAsserter implements JsonAsserter {
    private final String path;

    public ArrayIsEmptyAsserter(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object result) {
        if(result instanceof JSONArray){

            final JSONArray actualArrayValue = (JSONArray) result;

            if(actualArrayValue.isEmpty()){

                return AssertionReport.createFieldMatchesReport();
            }

            return AssertionReport.createFieldDoesNotMatchReport(path, "[]", result);

        } else {

            return AssertionReport.createFieldDoesNotMatchReport(path, "[]", result);

        }
    }
}
