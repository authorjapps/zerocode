
package org.jsmart.zerocode.core.engine.assertion.array;

import net.minidev.json.JSONArray;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

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
        if(result instanceof JSONArray){

            final JSONArray actualArrayValue = (JSONArray) result;

            if(actualArrayValue.isEmpty()){

                return aMatchingMessage();
            }

            return aNotMatchingMessage(path, "[]", result);

        } else {

            return aNotMatchingMessage(path, "[]", result);

        }
    }
}
