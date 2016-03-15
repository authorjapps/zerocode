package org.jsmart.smarttester.core.utils;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SmartUtilsTest {


    @Test
    public void willGetJsonFileIntoA_JavaString() throws  Exception{
        String jsonDocumentAsString = SmartUtils.getJsonDocumentAsString("smart_test_cases/01_test_json_single_step.json", this);
        assertThat(jsonDocumentAsString, containsString("assertions"));
        assertThat(jsonDocumentAsString, containsString("request"));
        assertThat(jsonDocumentAsString, containsString("{"));
        assertThat(jsonDocumentAsString, containsString("}"));
    }

    @Test
    public void willReadAllfileNamesFrom_TestResource() throws Exception {
        List<String> allTestCaseFiles = SmartUtils.getAllEndPointFiles("smart_test_cases");
        assertThat(allTestCaseFiles.size(), is(4));
        assertThat(allTestCaseFiles.get(0).toString(), is("smart_test_cases/01_test_json_single_step.json"));
    }
}