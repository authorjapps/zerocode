
package org.jsmart.zerocode.core.engine.assertion.array;

import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import java.util.List;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_EQUAL_TO_NUMBER;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_GREATER_THAN;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_LESSER_THAN;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_NOT_EQUAL_TO_NUMBER;

public class ArraySizeAsserterImpl implements JsonAsserter {
    private final String path;
    private final int expectedSize;
    private final String expectedSizeExpression;

    public ArraySizeAsserterImpl(String path, int size) {
        this.path = path;
        expectedSize = size;
        expectedSizeExpression = null;
    }

    public ArraySizeAsserterImpl(String path, String expression) {
        this.path = path;
        expectedSizeExpression = expression;
        expectedSize = -1;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getExpected() {
        return expectedSize;
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;

            if (this.expectedSize == -1 && this.expectedSizeExpression != null) {

                return processRelationalExpression(list);

            }

            if (list.size() == this.expectedSize) {

                return aMatchingMessage();
            }

            return aNotMatchingMessage(
                    path,
                    String.format("Array of size %d", expectedSize),
                    list.size());

        } else {

            return aNotMatchingMessage(path, "[]", result);

        }
    }

    public FieldAssertionMatcher processRelationalExpression(List<?> list) {
        if (expectedSizeExpression.startsWith(ASSERT_VALUE_GREATER_THAN)) {
            String greaterThan = this.expectedSizeExpression.substring(ASSERT_VALUE_GREATER_THAN.length());
            if (list.size() > Integer.parseInt(greaterThan)) {
                return aMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(ASSERT_VALUE_LESSER_THAN)) {
            String lesserThan = this.expectedSizeExpression.substring(ASSERT_VALUE_LESSER_THAN.length());
            if (list.size() < Integer.parseInt(lesserThan)) {
                return aMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(ASSERT_VALUE_EQUAL_TO_NUMBER)) {
            String equalTo = this.expectedSizeExpression.substring(ASSERT_VALUE_EQUAL_TO_NUMBER.length());
            if (list.size() == Integer.parseInt(equalTo)) {
                return aMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER)) {
            String notEqualTo = this.expectedSizeExpression.substring(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER.length());
            if (list.size() != Integer.parseInt(notEqualTo)) {
                return aMatchingMessage();
            }
        }

        return aNotMatchingMessage(
                path,
                String.format("Array of size %s", expectedSizeExpression),
                list.size());
    }


}
