package org.jsmart.zerocode.core.engine.assertion;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createNotMatchingMessage;

public class FieldHasGreaterThanValueAsserter implements JsonAsserter {
    private final String path;
    private final Number expected;

    public FieldHasGreaterThanValueAsserter(String path, Number expected) {
        this.path = path;
        this.expected = expected;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
        boolean areEqual;

        if (result instanceof Number && expected instanceof Number) {
            NumberComparator comparator = new NumberComparator();
            areEqual = comparator.compare((Number) result, (Number) expected) == 1;

        }
        else if (result == null && expected == null) {
            areEqual = true;

        }
        else if (result == null) {
            areEqual = false;

        }
        else {
            areEqual = false;

        }


        return areEqual ?
                createMatchingMessage() :
                createNotMatchingMessage(path, "Greater Than:" + expected, result);
    }
}

