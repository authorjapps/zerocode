package org.jsmart.zerocode.core.engine.assertion;

public class FieldIsNullAsserter implements JsonAsserter {
    private final String path;

    public FieldIsNullAsserter(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object result) {
        return result == null ?
                AssertionReport.createFieldMatchesReport() :
                AssertionReport.createFieldDoesNotMatchReport(path, "NULL", result);
    }
}
