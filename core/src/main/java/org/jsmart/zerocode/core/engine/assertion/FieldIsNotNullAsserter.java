package org.jsmart.zerocode.core.engine.assertion;

public class FieldIsNotNullAsserter implements JsonAsserter {
    private final String path;

    public FieldIsNotNullAsserter(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object result) {
        return result != null ?
                AssertionReport.createFieldMatchesReport() :
                AssertionReport.createFieldDoesNotMatchReport(path, "NOT NULL", result);
    }
}
