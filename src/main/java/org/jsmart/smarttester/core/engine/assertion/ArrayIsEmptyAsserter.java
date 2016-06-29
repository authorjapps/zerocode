package org.jsmart.smarttester.core.engine.assertion;

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
        if(result instanceof net.minidev.json.JSONArray){

            final net.minidev.json.JSONArray actualArrayValue = (net.minidev.json.JSONArray) result;

            if(actualArrayValue.isEmpty()){

                return AssertionReport.createFieldMatchesReport();
            }

            return AssertionReport.createFieldDoesNotMatchReport(path, "[]", result);

        } else {

            return AssertionReport.createFieldDoesNotMatchReport(path, "[]", result);

        }
    }
}
