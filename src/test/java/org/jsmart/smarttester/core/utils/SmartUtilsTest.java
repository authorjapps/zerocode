package org.jsmart.smarttester.core.utils;

import com.google.inject.Inject;
import org.jsmart.smarttester.core.di.SmartServiceModule;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
@UseModules(SmartServiceModule.class)
public class SmartUtilsTest {

    @Inject //<---- Without this inject you can not have the ObjectMapper injected inside SmartUtils. Also you cant have the Object mapper as static.
    SmartUtils smartUtils;

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

    @Test
    public void testGetItRight_Guice() throws Exception {
        assertThat(smartUtils.getItRight(), notNullValue());
    }
}