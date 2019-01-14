package org.jsmart.zerocode.core.engine.assertion;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface JsonAsserter {
    Logger logger = LoggerFactory.getLogger(JsonAsserter.class);

    /*
     * Every asserter must provide implementation and
     * return the field-path whose value is to be asserted
     */
    String getPath();

    AssertionReport actualEqualsToExpected(Object result);

    default AssertionReport assertWithJson(String jsonSource) {

        Object result = null;
        try{
            result = JsonPath.read(jsonSource, getPath());

        } catch(PathNotFoundException pEx){

            logger.warn("Path: {} was not found in the response. Hence this value was treated as null.", getPath());

        }

        return actualEqualsToExpected(result);
    }


}
