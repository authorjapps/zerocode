package org.jsmart.zerocode.core.engine.assertion;

public class FieldHasSubStringValueAsserter implements JsonAsserter {
    private final String path;
    private final String expected;

    public FieldHasSubStringValueAsserter(String path, String expected) {
        this.path = path;
        this.expected = expected;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object result) {
        boolean areEqual;
        if (result instanceof String && expected instanceof String) {
            String s1 = (String) result;
            String s2 = expected;
            areEqual = s1.contains(s2);
        } else {
            areEqual = false;
        }

        return areEqual ?
                AssertionReport.createFieldMatchesReport() :
                AssertionReport.createFieldDoesNotMatchReport(path, "containing sub-string:" + expected, result);
    }
}
