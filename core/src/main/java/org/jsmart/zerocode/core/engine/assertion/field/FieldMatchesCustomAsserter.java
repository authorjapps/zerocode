
package org.jsmart.zerocode.core.engine.assertion.field;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaCustomExecutor;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;

public class FieldMatchesCustomAsserter implements JsonAsserter {
    private final String path;
    private final String expected;
    ObjectMapper mapper = new ObjectMapperProvider().get();

    public FieldMatchesCustomAsserter(String path, String expected) {
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
    public FieldAssertionMatcher actualEqualsToExpected(Object actual) {
        boolean areEqual = JavaCustomExecutor.executeMethod(expected, actual);

        return areEqual ?
                aMatchingMessage() :
                aNotMatchingMessage(path, " custom assertion:" + expected, actual);
    }
}
