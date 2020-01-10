package org.jsmart.zerocode.core.utils;

import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jukito.JukitoRunner;
import org.jukito.TestModule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;

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
        Step stepJava = smartUtils.scenarioFileToJava("unit_test_files/engine_unit_test_jsons/01_test_json_single_step.json", Step.class);
        assertThat(stepJava.getLoop(), is(3));

        ScenarioSpec scenarioJava = smartUtils.scenarioFileToJava("unit_test_files/engine_unit_test_jsons/02_test_json_flow_single_step.json", ScenarioSpec.class);
        assertThat(scenarioJava.getLoop(), is(5));
    }

    @Test
    public void willGetJsonFileIntoA_JavaString() throws Exception {
        String jsonDocumentAsString = smartUtils.getJsonDocumentAsString("unit_test_files/engine_unit_test_jsons/01_test_json_single_step.json");
        assertThat(jsonDocumentAsString, containsString("assertions"));
        assertThat(jsonDocumentAsString, containsString("request"));
        assertThat(jsonDocumentAsString, containsString("{"));
        assertThat(jsonDocumentAsString, containsString("}"));
    }

    @Test
    public void willReadAllfileNamesFrom_TestResource() throws Exception {
        List<String> allTestCaseFiles = SmartUtils.getAllEndPointFiles("unit_test_files/engine_unit_test_jsons");
        assertThat(allTestCaseFiles.size(), is(18));
        assertThat(allTestCaseFiles.get(0), is("unit_test_files/engine_unit_test_jsons/00_test_json_single_step_verifications.json"));
    }

    @Test
    public void willReadAllfileNames_AND_return_FlowSpecList() throws Exception {
        List<ScenarioSpec> allTestCaseFiles = smartUtils.getScenarioSpecListByPackage("unit_test_files/test_scenario_cases");

        assertThat(allTestCaseFiles.size(), is(3));
        assertThat(allTestCaseFiles.get(0).getScenarioName(), is("Given_When_Then_1"));
        assertThat(allTestCaseFiles.get(2).getScenarioName(), is("Given_When_Then-Flow2"));
    }


    @Test(expected = RuntimeException.class)
    public void willReadAllfiles_find_DuplicatesScenarioNamenames_old_style() throws Exception {
        smartUtils.checkDuplicateScenarios("unit_test_files/test_scenario_cases");
    }

    @Test
    public void willReadAllfiles_find_DuplicateScenarioNames() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Oops! Can not run with multiple Scenarios with same name.");
        smartUtils.checkDuplicateScenarios("unit_test_files/test_scenario_cases");
    }

    @Test
    public void willEvaluatePlaceHolder() throws Exception {

        String aString = "Hello_${WORLD}";
        List<String> placeHolders = getTestCaseTokens(aString);
        Assert.assertThat(placeHolders.size(), is(1));
        Assert.assertThat(placeHolders.get(0), is("WORLD"));

        aString = "Hello_${$.step_name}";
        placeHolders = getTestCaseTokens(aString);
        Assert.assertThat(placeHolders.size(), is(1));
        Assert.assertThat(placeHolders.get(0), is("$.step_name"));

    }

    @Test
    public void testNullOrEmptyString_withPlaceHolders() throws Exception {

        String aString = "";
        List<String> placeHolders = getTestCaseTokens(aString);
        Assert.assertThat(placeHolders.size(), is(0));

        aString = "Hello_";
        placeHolders = getTestCaseTokens(aString);
        Assert.assertThat(placeHolders.size(), is(0));
    }

    @Test
    public void testReplaceTokensOrPlaceHolders() throws Exception {
        String aString = "_${ENV_PROPERTY_NAME}";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ENV_PROPERTY_NAME", "ci2");

        final String resolvedString = SmartUtils.resolveToken(aString, paramMap);

        assertThat(resolvedString, is("_ci2"));
    }

    @Test
    public void testEnvValue() throws Exception {

        final String javaHomeValue = SmartUtils.getEnvPropertyValue("JAVA_HOME");
        assertThat(javaHomeValue, notNullValue());

        assertThat(SmartUtils.getEnvPropertyValue("WRONG_XYZ_INVALID"), nullValue());
    }

    @Test
    public void testSuiteFolder_absolutePath() throws Exception {
        // Try in target folder
        String folder1File1 = "target/temp/unit_test_files/cherry_pick_tests/folder_a/test_case_1.json";
        String folder2File2 = "target/temp/unit_test_files/cherry_pick_tests/folder_b/test_case_2.json";

        // Create the folders
        createCascadeIfNotExisting(folder1File1);
        createCascadeIfNotExisting(folder2File2);

        Path path1 = Paths.get(folder1File1);
        Path path2 = Paths.get(folder2File2);
        String absolutePath1 = path1.toFile().getAbsolutePath();
        String absolutePath2 = path2.toFile().getAbsolutePath();

        // Create the files
        File f1 = new File(absolutePath1);
        File f2 = new File(absolutePath2);
        f1.createNewFile();
        f2.createNewFile();


        String parentFolderAbsPath = path1.getParent().getParent().toFile().getAbsolutePath();

        List<String> allScenarios = SmartUtils.retrieveScenariosByAbsPath(parentFolderAbsPath);
        assertThat(allScenarios.size(), is(2));
        assertThat(allScenarios.get(0), containsString("unit_test_files/cherry_pick_tests/folder_b/test_case_2.json"));
        assertThat(allScenarios.get(1), containsString("unit_test_files/cherry_pick_tests/folder_a/test_case_1.json"));

        // Delete the folders/files
        // mvn clean
    }

    @Ignore("Tested in local laptop. Ignored for Ci build. Follow testSuiteFolder_absolutePath() like flow ")
    @Test
    public void testSuiteFolder_symAbsolutePath() throws Exception {
        String absPath = "~/dev/ZEROCODE_REPOS/zerocode/core/src/test/resources/unit_test_files/cherry_pick_tests";
        List<String> allScenarios = SmartUtils.retrieveScenariosByAbsPath(absPath);
        assertThat(allScenarios.size(), is(2));
        assertThat(allScenarios.get(0), containsString("unit_test_files/cherry_pick_tests/folder_b/test_case_2.json"));
        assertThat(allScenarios.get(0), containsString("cherry_pick_tests/folder_b/test_case_2.json"));
    }

    // Move this to File Util class
    private static File createCascadeIfNotExisting(String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.createDirectories(path.getParent());

            File file = new File(fileName);

            return file;
        } catch (IOException exx) {
            throw new RuntimeException("Create file '" + fileName + "' Exception" + exx);
        }
    }
}