

package org.jsmart.zerocode.core.engine.assertion.field;

import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

public class FieldIsNotNullAsserter implements JsonAsserter {
    private final String path;

    public FieldIsNotNullAsserter(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getExpected() {
        return "NOT NULL";
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object actualResult) {

        return defaultAssertionMessage(actualResult, actualResult != null);
    }
}
