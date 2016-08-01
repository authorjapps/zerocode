package org.jsmart.zerocode.core.engine.assertion;

public class FieldHasInEqualNumberValueAsserter implements JsonAsserter {
    private final String path;
    private final Number expected;

    public FieldHasInEqualNumberValueAsserter(String path, Number expected) {
        this.path = path;
        this.expected = expected;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object result) {
        boolean areNotEqual;

        if (result instanceof Number && expected instanceof Number) {
            NumberComparator comparator = new NumberComparator();
            areNotEqual = comparator.compare((Number) result, (Number) expected) != 0;

        } else if (result == null && expected == null) {
            areNotEqual = false;

        } else if (result == null) {
            areNotEqual = true;

        } else {
            areNotEqual = true;

        }

        return areNotEqual ?
                AssertionReport.createFieldMatchesReport() :
                AssertionReport.createFieldDoesNotMatchReport(path, "not equals to " + expected, result);
    }
}

