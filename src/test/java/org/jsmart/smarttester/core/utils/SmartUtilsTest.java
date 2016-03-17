package org.jsmart.smarttester.core.utils;

import com.google.inject.Inject;
import org.jsmart.smarttester.core.di.SmartServiceModule;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.domain.Step;
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
    public void testGetItRight_Guice() throws Exception {
        assertThat(smartUtils.getItRight(), notNullValue());
    }

    @Test
    public void testJsonToJavaFor_jsonFileName() throws Exception {
        Step stepJava = smartUtils.jsonToJava("smart_test_cases/01_test_json_single_step.json", Step.class);
        assertThat(stepJava.getLoop(), is(3));

        FlowSpec flowJava = smartUtils.jsonToJava("smart_test_cases/02_test_json_flow_single_step.json", FlowSpec.class);
        assertThat(flowJava.getLoop(), is(5));
    }

    @Test
    public void willGetJsonFileIntoA_JavaString() throws  Exception{
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("smart_test_cases/01_test_json_single_step.json");
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
    public void willReadAllfileNames_AND_return_FlowSpecList() throws Exception {
        List<FlowSpec> allTestCaseFiles = smartUtils.getFlowSpecListByPackage("test_flow_cases");

        assertThat(allTestCaseFiles.size(), is(2));
        assertThat(allTestCaseFiles.get(0).getFlowName(), is("Given_When_Then_1"));
        assertThat(allTestCaseFiles.get(1).getFlowName(), is("Given_When_Then-Flow2"));
    }

    /*@Test
    public void willReadAllfiles_and_ReadTheContents_And_find_Duplicates() throws Exception {
        List<String> allTestCaseFiles = SmartUtils.getAllEndPointFiles("smart_test_cases");

        allTestCaseFiles.stream()
                .forEach(flowSpecFile -> {
                    try {
                        System.out.println(SmartUtils.getJsonDocumentAsString(flowSpecFile, this));
                    } catch (IOException e) {
                        throw new RuntimeException("Exception Details: " + e);
                    }
                });

    }*/
}