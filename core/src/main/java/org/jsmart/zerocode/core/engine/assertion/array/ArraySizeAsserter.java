
package org.jsmart.zerocode.core.engine.assertion.array;

import net.minidev.json.JSONArray;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;
import static org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessorImpl.*;

public class ArraySizeAsserter implements JsonAsserter {
    private final String path;
    private final int expectedSize;
    private final String expectedSizeExpression;

    public ArraySizeAsserter(String path, int size) {
        this.path = path;
        expectedSize = size;
        expectedSizeExpression = null;
    }

    public ArraySizeAsserter(String path, String expression) {
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
        if (result instanceof JSONArray) {

            final JSONArray actualArrayValue = (JSONArray) result;

            if (this.expectedSize == -1 && this.expectedSizeExpression != null) {

                return processRelationalExpression(actualArrayValue);

            }

            if (actualArrayValue.size() == this.expectedSize) {

                return aMatchingMessage();
            }

            return aNotMatchingMessage(
                    path,
                    String.format("Array of size %d", expectedSize),
                    actualArrayValue.size());

        } else {

            return aNotMatchingMessage(path, "[]", result);

        }
    }

    public FieldAssertionMatcher processRelationalExpression(JSONArray actualArrayValue) {
        if (expectedSizeExpression.startsWith(ASSERT_VALUE_GREATER_THAN)) {
            String greaterThan = this.expectedSizeExpression.substring(ASSERT_VALUE_GREATER_THAN.length());
            if (actualArrayValue.size() > Integer.parseInt(greaterThan)) {
                return aMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(ASSERT_VALUE_LESSER_THAN)) {
            String lesserThan = this.expectedSizeExpression.substring(ASSERT_VALUE_LESSER_THAN.length());
            if (actualArrayValue.size() < Integer.parseInt(lesserThan)) {
                return aMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(ASSERT_VALUE_EQUAL_TO_NUMBER)) {
            String equalTo = this.expectedSizeExpression.substring(ASSERT_VALUE_EQUAL_TO_NUMBER.length());
            if (actualArrayValue.size() == Integer.parseInt(equalTo)) {
                return aMatchingMessage();
            }
        } else if (expectedSizeExpression.startsWith(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER)) {
            String notEqualTo = this.expectedSizeExpression.substring(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER.length());
            if (actualArrayValue.size() != Integer.parseInt(notEqualTo)) {
                return aMatchingMessage();
            }
        }

        return aNotMatchingMessage(
                path,
                String.format("Array of size %s", expectedSizeExpression),
                actualArrayValue.size());
    }


}
