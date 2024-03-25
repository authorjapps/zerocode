
package org.jsmart.zerocode.core.engine.assertion.array;

import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import java.util.List;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;

public class ArrayIsEmptyAsserterImpl implements JsonAsserter {
    private final String path;

    public ArrayIsEmptyAsserterImpl(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getExpected() {
        return "[]";
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;

            if (list.isEmpty()) {
                return aMatchingMessage();
            }

        }
        return aNotMatchingMessage(path, "[]", result);
    }
}
