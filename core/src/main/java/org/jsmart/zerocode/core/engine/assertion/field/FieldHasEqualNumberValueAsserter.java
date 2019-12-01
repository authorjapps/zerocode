
package org.jsmart.zerocode.core.engine.assertion.field;

import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.NumberComparator;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

public class FieldHasEqualNumberValueAsserter implements JsonAsserter {
    private final String path;
    private final Number expected;

    public FieldHasEqualNumberValueAsserter(String path, Number expected) {
        this.path = path;
        this.expected = expected;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getExpected() {
        return expected;
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object actualResult) {
        boolean areEqual;

        if (actualResult instanceof Number && expected instanceof Number) {
            NumberComparator comparator = new NumberComparator();
            areEqual = comparator.compare((Number) actualResult, (Number) expected) == 0;

        } else if (actualResult == null && expected == null) {
            areEqual = true;

        } else if (actualResult == null) {
            areEqual = false;

        } else {
            areEqual = false;

        }

        return defaultAssertionMessage(actualResult, areEqual);
    }
}

