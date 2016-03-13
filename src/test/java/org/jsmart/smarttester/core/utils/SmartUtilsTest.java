package org.jsmart.smarttester.core.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class SmartUtilsTest {


    @Test
    public void willGetJsonFileIntoAJavaString() throws  Exception{
        String jsonDocumentAsString = SmartUtils.getJsonDocumentAsString("smart_test_cases/01_test_json_single_step.json", this);
        assertThat(jsonDocumentAsString, containsString("assertions"));
        assertThat(jsonDocumentAsString, containsString("request"));
        assertThat(jsonDocumentAsString, containsString("{"));
        assertThat(jsonDocumentAsString, containsString("}"));
    }

}