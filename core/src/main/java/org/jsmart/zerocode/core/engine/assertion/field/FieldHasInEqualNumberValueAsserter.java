
package org.jsmart.zerocode.core.engine.assertion.field;

import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.NumberComparator;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createNotMatchingMessage;

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
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
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
                createMatchingMessage() :
                createNotMatchingMessage(path, "not equals to " + expected, result);
    }
}

