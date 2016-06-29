package org.jsmart.smarttester.core.engine.assertion;

public class FieldHasExactValueAsserter implements JsonAsserter {
    private final String path;
    final Object expected;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public AssertionReport actualEqualsToExpected(Object actualResult) {
        boolean areEqual;

        if (actualResult instanceof Number && expected instanceof Number) {
            NumberComparator comparator = new NumberComparator();
            areEqual = comparator.compare((Number) expected, (Number) actualResult) == 0;

        } else if (actualResult == null && expected == null) {
            areEqual = true;

        } else if (actualResult != null) {
            areEqual = actualResult.equals(expected);

        } else {
            areEqual = false;

        }

        return areEqual ?
                AssertionReport.createFieldMatchesReport() :
                AssertionReport.createFieldDoesNotMatchReport(path, expected, actualResult);
    }

    public FieldHasExactValueAsserter(String path, Object expected) {
        this.path = path;
        this.expected = expected;
    }

}
