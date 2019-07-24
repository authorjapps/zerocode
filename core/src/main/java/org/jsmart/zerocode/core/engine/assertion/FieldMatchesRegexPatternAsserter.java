package org.jsmart.zerocode.core.engine.assertion;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createNotMatchingMessage;

public class FieldMatchesRegexPatternAsserter implements JsonAsserter {
    private final String path;
    private final String expected;

    public FieldMatchesRegexPatternAsserter(String path, String expected) {
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
        if (result instanceof String && expected instanceof String) {
            String s1 = (String) result;
            String s2 = expected;
            areEqual = s1.matches(s2);
        } else {
            areEqual = false;
        }

        return areEqual ?
                createMatchingMessage() :
                createNotMatchingMessage(path, "containing sub-string:" + expected, result);
    }
}
