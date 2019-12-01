
package org.jsmart.zerocode.core.engine.assertion.field;

import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;

public class FieldContainsStringAsserter implements JsonAsserter {
    private final String path;
    private final String expected;

    public FieldContainsStringAsserter(String path, String expected) {
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
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
        boolean areEqual;
        if (result instanceof String && expected instanceof String) {
            String s1 = (String) result;
            String s2 = expected;
            areEqual = s1.contains(s2);
        } else {
            areEqual = false;
        }

        return areEqual ?
                aMatchingMessage() :
                aNotMatchingMessage(path, "containing sub-string:" + expected, result);
    }
}
