package org.jsmart.zerocode.core.engine.assertion;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;

public interface JsonAsserter {
    Logger logger = LoggerFactory.getLogger(JsonAsserter.class);

    /*
     * Every asserter must provide implementation and
     * return the field-path whose value is to be asserted
     */
    String getPath();

    /*
     * Every asserter must provide implementation of the expected value
     */
    Object getExpected();

    FieldAssertionMatcher actualEqualsToExpected(Object result);

    default FieldAssertionMatcher assertWithJson(String jsonSource) {

        Object result = null;
        try{
            result = JsonPath.read(jsonSource, getPath());

        } catch(PathNotFoundException pEx){

            logger.warn("Path: {} was not found in the response. Hence this value was treated as null.", getPath());

        }

        return actualEqualsToExpected(result);
    }

    default FieldAssertionMatcher defaultAssertionMessage(Object actualResult, boolean areEqual) {
        return areEqual ? aMatchingMessage() : aNotMatchingMessage(getPath(), getExpected(), actualResult);
    }

}
