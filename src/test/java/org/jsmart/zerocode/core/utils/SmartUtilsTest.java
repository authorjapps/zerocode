package org.jsmart.zerocode.core.utils;

import com.google.inject.Inject;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
//@UseModules(ApplicationMainModule.class) //<--- Only if you dont pass any value to it's constructor
public class SmartUtilsTest {

    public static class JukitoModule extends TestModule {
        @Override
        protected void configureTest() {
            ApplicationMainModule applicationMainModule = new ApplicationMainModule("config_hosts_test.properties");

            /* Finally install the main module */
            install(applicationMainModule);
        }
    }

    @Inject //<---- Without this inject you can not have the ObjectMapper injected inside SmartUtils. Also you cant have the Object mapper as static.
    SmartUtils smartUtils;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void testGetItRight_Guice() throws Exception {
        assertThat(smartUtils.getItRight(), notNullValue());
    }

    @Test
    public void testJsonToJavaFor_jsonFileName() throws Exception {
        Step stepJava = smartUtils.jsonFileToJava("01_test_smart_test_cases/01_test_json_single_step.json", Step.class);
        assertThat(stepJava.getLoop(), is(3));

        ScenarioSpec flowJava = smartUtils.jsonFileToJava("01_test_smart_test_cases/02_test_json_flow_single_step.json", ScenarioSpec.class);
        assertThat(flowJava.getLoop(), is(5));
    }

    @Test
    public void willGetJsonFileIntoA_JavaString() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("01_test_smart_test_cases/01_test_json_single_step.json");
        assertThat(jsonDocumentAsString, containsString("assertions"));
        assertThat(jsonDocumentAsString, containsString("request"));
        assertThat(jsonDocumentAsString, containsString("{"));
        assertThat(jsonDocumentAsString, containsString("}"));
    }

    @Test
    public void willReadAllfileNamesFrom_TestResource() throws Exception {
        List<String> allTestCaseFiles = SmartUtils.getAllEndPointFiles("01_test_smart_test_cases");
        assertThat(allTestCaseFiles.size(), is(4));
        assertThat(allTestCaseFiles.get(0).toString(), is("01_test_smart_test_cases/01_test_json_single_step.json"));
    }

    @Test
    public void willReadAllfileNames_AND_return_FlowSpecList() throws Exception {
        List<ScenarioSpec> allTestCaseFiles = smartUtils.getScenarioSpecListByPackage("04_test_flow_cases");

        assertThat(allTestCaseFiles.size(), is(3));
        assertThat(allTestCaseFiles.get(0).getScenarioName(), is("Given_When_Then_1"));
        assertThat(allTestCaseFiles.get(2).getScenarioName(), is("Given_When_Then-Flow2"));
    }


    @Test(expected = RuntimeException.class)
    public void willReadAllfiles_find_DuplicatesScenarioNamenames_old_style() throws Exception {
        smartUtils.checkDuplicateScenarios("04_test_flow_cases");
    }

    @Test
    public void willReadAllfiles_find_DuplicateScenarioNames() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Oops! Can not run with multiple Scenarios with same name.");
        smartUtils.checkDuplicateScenarios("04_test_flow_cases");
    }
}