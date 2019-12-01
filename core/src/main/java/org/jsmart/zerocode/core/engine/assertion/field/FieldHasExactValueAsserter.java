
package org.jsmart.zerocode.core.engine.assertion.field;

import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.NumberComparator;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

public class FieldHasExactValueAsserter implements JsonAsserter {
    private final String path;
    final Object expected;

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

        /*
         * Any number
         */
        if (actualResult instanceof Number && expected instanceof Number) {
            NumberComparator comparator = new NumberComparator();
            areEqual = comparator.compare((Number) expected, (Number) actualResult) == 0;

        }
        /*
         * Both are null
         */
        else if (actualResult == null && expected == null) {
            areEqual = true;

        }
        /*
         * Any String
         */
        else if (actualResult != null) {
            areEqual = actualResult.equals(expected);

        }
        else {
            areEqual = false;

        }

        return defaultAssertionMessage(actualResult, areEqual);
    }

    public FieldHasExactValueAsserter(String path, Object expected) {
        this.path = path;
        this.expected = expected;
    }

}
