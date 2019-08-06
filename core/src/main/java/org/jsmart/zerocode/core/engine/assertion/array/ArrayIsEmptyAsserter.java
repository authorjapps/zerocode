
package org.jsmart.zerocode.core.engine.assertion.array;

import net.minidev.json.JSONArray;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createNotMatchingMessage;

public class ArrayIsEmptyAsserter implements JsonAsserter {
    private final String path;

    public ArrayIsEmptyAsserter(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
        if(result instanceof JSONArray){

            final JSONArray actualArrayValue = (JSONArray) result;

            if(actualArrayValue.isEmpty()){

                return createMatchingMessage();
            }

            return createNotMatchingMessage(path, "[]", result);

        } else {

            return createNotMatchingMessage(path, "[]", result);

        }
    }
}
