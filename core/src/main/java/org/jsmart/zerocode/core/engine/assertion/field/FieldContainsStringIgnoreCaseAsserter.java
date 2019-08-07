
package org.jsmart.zerocode.core.engine.assertion.field;

import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

public class FieldContainsStringIgnoreCaseAsserter implements JsonAsserter {
    private final String path;
    private final String expected;

    public FieldContainsStringIgnoreCaseAsserter(String path, String expected) {
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
            areEqual = s1.toUpperCase().contains(s2.toUpperCase());
        } else {
            areEqual = false;
        }

        return areEqual ?
                FieldAssertionMatcher.aMatchingMessage() :
                FieldAssertionMatcher.aNotMatchingMessage(path, "containing sub-string with ignoring case:" + expected, result);
    }
}
