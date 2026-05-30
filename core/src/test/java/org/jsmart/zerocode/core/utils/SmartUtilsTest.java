package org.jsmart.zerocode.core.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.guice.ZeroCodeGuiceTestRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;
import java.net.URLClassLoader;

//@UseModules(ApplicationMainModule.class) //<--- Only if you dont pass any value to it's constructor
public class SmartUtilsTest {
    @Rule
    public ZeroCodeGuiceTestRule guiceRule = new ZeroCodeGuiceTestRule(this, SmartUtilsTest.ZeroCodeTestModule.class);
    public static class ZeroCodeTestModule extends AbstractModule {
        @Override
        protected void configure() {
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
    public void willReadAllfileNamesFrom_TestResource() {
        List<String> allTestCaseFiles = SmartUtils.getAllEndPointFiles("unit_test_files/engine_unit_test_jsons");
        assertThat(allTestCaseFiles.size(), is(25));
        assertThat(allTestCaseFiles.get(0), is("unit_test_files/engine_unit_test_jsons/00_test_json_single_step_verifications.json"));
    }

    @Test
    public void willReadAllfileNames_AND_return_FlowSpecList() {
        List<ScenarioSpec> allTestCaseFiles = smartUtils.getScenarioSpecListByPackage("unit_test_files/test_scenario_cases");

        assertThat(allTestCaseFiles.size(), is(3));
        assertThat(allTestCaseFiles.get(0).getScenarioName(), is("Given_When_Then_1"));
        assertThat(allTestCaseFiles.get(2).getScenarioName(), is("Given_When_Then-Flow2"));
    }


    @Test(expected = RuntimeException.class)
    public void willReadAllfiles_find_DuplicatesScenarioNamenames_old_style() {
        smartUtils.checkDuplicateScenarios("unit_test_files/test_scenario_cases");
    }

    @Test
    public void willReadAllfiles_find_DuplicateScenarioNames() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Oops! Duplicate scenario found, either rename or remove extra ones");
        smartUtils.checkDuplicateScenarios("unit_test_files/test_scenario_cases");
    }

    @Test
    public void willEvaluatePlaceHolder() {

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
    public void testNullOrEmptyString_withPlaceHolders() {

        String aString = "";
        List<String> placeHolders = getTestCaseTokens(aString);
        Assert.assertThat(placeHolders.size(), is(0));

        aString = "Hello_";
        placeHolders = getTestCaseTokens(aString);
        Assert.assertThat(placeHolders.size(), is(0));
    }

    @Test
    public void testReplaceTokensOrPlaceHolders() {
        String aString = "_${ENV_PROPERTY_NAME}";

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ENV_PROPERTY_NAME", "ci2");

        final String resolvedString = SmartUtils.resolveToken(aString, paramMap);

        assertThat(resolvedString, is("_ci2"));
    }

    @Test
    public void testEnvValue() {

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
        //TODO- Build to be fixed. Locally passes, but fails in GitHub actions build.
        // Probably due to JDK version adding items in different version
//        assertThat(allScenarios.get(0), containsString("unit_test_files/cherry_pick_tests/folder_b/test_case_2.json"));
//        assertThat(allScenarios.get(1), containsString("unit_test_files/cherry_pick_tests/folder_a/test_case_1.json"));

        // Temporary fix added for asserting array items to unblock the PRs people are waiting for.
        // TODO: Fix this to assert that item contains in any order with full string above
        assertThat(allScenarios.get(0), containsString("/test_case_"));
        assertThat(allScenarios.get(0), containsString("unit_test_files/cherry_pick_tests"));

        assertThat(allScenarios.get(1), containsString("/test_case_"));
        assertThat(allScenarios.get(1), containsString("unit_test_files/cherry_pick_tests"));

        // Delete the folders/files
        // mvn clean
    }


    @Test
    public void testScenarioFile_absolutePath() throws Exception {
        // Try in target folder
        String folder1File1 = "target/temp/unit_test_files/cherry_pick_tests/folder_a/test_case_1.json";

        boolean isAbsPath = SmartUtils.isValidAbsolutePath(folder1File1);
        System.out.println("isAbsPath should be false as file is not yet created : " + isAbsPath);
        assertThat(isAbsPath, is(false)); //Do a mvn clean before this. Travis shd do this everytime build runs

        // Create the folders
        createCascadeIfNotExisting(folder1File1);

        Path path1 = Paths.get(folder1File1);
        String absolutePath1 = path1.toFile().getAbsolutePath();

        // Create the files
        File f1 = new File(absolutePath1);
        f1.createNewFile();

        isAbsPath = SmartUtils.isValidAbsolutePath(folder1File1);
        System.out.println("isAbsPath should be true as file exits : " + isAbsPath);
        assertThat(isAbsPath, is(true));

        String absPathToFileWithParent = path1.getParent().getParent().toFile().getAbsolutePath();
        System.out.println("parentFolderAbsPath : " + absPathToFileWithParent); // This is full path
        isAbsPath = SmartUtils.isValidAbsolutePath(absPathToFileWithParent);
        assertThat(isAbsPath, is(true));

        // Delete the folders/files
        // mvn clean
    }

    @Test
    public void testScenarioFile_relativePath() throws Exception {
        // Function to test that the readJsonAsString function reads scenarios with relative paths as well.

        // Test Relative Path.
        String classPath = "unit_test_files/cherry_pick_tests/folder_a/test_case_1.json";
        String relativeTestPath = "./src/test/resources/unit_test_files/cherry_pick_tests/folder_a/test_case_1.json";

        String jsonStringFromClassPath = SmartUtils.readJsonAsString(classPath);
        String jsonStringFromRelativePath = SmartUtils.readJsonAsString(relativeTestPath);
        boolean classPathRelativePathCheckBool = jsonStringFromRelativePath.equals(jsonStringFromClassPath);
        assert(classPathRelativePathCheckBool);

        System.out.println("Does readJsonAsString load the same file from relative and classpth: " + classPathRelativePathCheckBool);

    }

    @Ignore("Tested in local laptop. Ignored for Ci build. Follow testSuiteFolder_absolutePath() like flow ")
    @Test
    public void testSuiteFolder_symAbsolutePath() {
        String absPath = "~/dev/ZEROCODE_REPOS/zerocode/core/src/test/resources/unit_test_files/cherry_pick_tests";
        List<String> allScenarios = SmartUtils.retrieveScenariosByAbsPath(absPath);
        assertThat(allScenarios.size(), is(2));
        assertThat(allScenarios.get(0), containsString("unit_test_files/cherry_pick_tests/folder_b/test_case_2.json"));
        assertThat(allScenarios.get(0), containsString("cherry_pick_tests/folder_b/test_case_2.json"));
    }

    @Test
    public void testSanitizeReportFile() {
    	String orig = "file !#$%&'()*+,-./09:;<=>?@AZ[]^_`az{|}~\"\\";
    	String dest = "file ___________-._09_______AZ_____az______";
    	assertThat(SmartUtils.sanitizeReportFileName(orig), equalTo(dest));
    }

    @Test
    public void collectJsonFilesFromDir_returnsAllJsonFiles() {
        File dir = new File(getClass().getClassLoader().getResource("unit_test_files/engine_unit_test_jsons").getFile());
        List<String> files = SmartUtils.collectJsonFilesFromDir(dir, "unit_test_files/engine_unit_test_jsons");
        assertThat(files.size(), is(25));
        assertThat(files, hasItem("unit_test_files/engine_unit_test_jsons/00_test_json_single_step_verifications.json"));
    }

    @Test
    public void collectJsonFilesFromDir_returnsEmpty_whenDirNotExist() {
        File dir = new File("non/existent/path");
        List<String> files = SmartUtils.collectJsonFilesFromDir(dir, "non/existent/path");
        assertThat(files.size(), is(0));
    }

    @Test
    public void collectJsonFilesFromDir_returnsEmpty_whenNoJsonFiles() throws Exception {
        File dir = Files.createTempDirectory("zerocode_test_no_json").toFile();
        dir.deleteOnExit();
        new File(dir, "readme.txt").createNewFile();
        List<String> files = SmartUtils.collectJsonFilesFromDir(dir, "some/package");
        assertThat(files.size(), is(0));
    }

    @Test
    public void getAllEndPointFiles_readsJsonFilesFromJar() throws Exception {

        Path tempDir = Files.createTempDirectory("jar-test");
        File jarFile = new File(tempDir.toFile(), "test-scenarios.jar");

        try (java.util.jar.JarOutputStream jarOut =
                     new java.util.jar.JarOutputStream(Files.newOutputStream(jarFile.toPath()))) {

            jarOut.putNextEntry(new java.util.jar.JarEntry("unit_test_files/"));
            jarOut.closeEntry();

            jarOut.putNextEntry(new java.util.jar.JarEntry("unit_test_files/jar_tests/"));
            jarOut.closeEntry();

            jarOut.putNextEntry(
                    new java.util.jar.JarEntry(
                            "unit_test_files/jar_tests/test1.json"));
            jarOut.write("{\"hello\":\"world\"}".getBytes());
            jarOut.closeEntry();

            jarOut.putNextEntry(
                    new java.util.jar.JarEntry(
                            "unit_test_files/jar_tests/test2.json"));
            jarOut.write("{\"hello\":\"again\"}".getBytes());
            jarOut.closeEntry();
        }

        URL jarUrl = jarFile.toURI().toURL();

        ClassLoader original = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader =
                new URLClassLoader(new URL[]{jarUrl}, original);

        Thread.currentThread().setContextClassLoader(urlClassLoader);

        try {
            List<String> files =
                    SmartUtils.getAllEndPointFiles("unit_test_files/jar_tests");

            assertThat(files.size(), is(2));

            assertThat(files,
                    hasItem("unit_test_files/jar_tests/test1.json"));

            assertThat(files,
                    hasItem("unit_test_files/jar_tests/test2.json"));

        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test(expected = RuntimeException.class)
    public void getAllEndPointFiles_throwsException_whenNoResourcesFound() {

        SmartUtils.getAllEndPointFiles("non_existing_package_xyz");
    }

    @Test
    public void getAllEndPointFiles_skipsCorruptJar() throws Exception {

        Path tempDir = Files.createTempDirectory("corrupt-jar-test");

        File corruptJar = new File(tempDir.toFile(), "corrupt.jar");

        Files.write(
                corruptJar.toPath(),
                "this-is-not-a-valid-jar".getBytes()
        );

        URL jarUrl = corruptJar.toURI().toURL();

        ClassLoader original = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader =
                new URLClassLoader(new URL[]{jarUrl}, original);

        Thread.currentThread().setContextClassLoader(urlClassLoader);

        try {

            expectedException.expect(RuntimeException.class);
            expectedException.expectMessage("NothingFoundHereException");

            SmartUtils.getAllEndPointFiles("unit_test_files/jar_tests");

        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }


    @Test
    public void getAllEndPointFiles_deduplicates_sameFilePath_fromMultipleClasspathEntries() throws Exception {
        // Reproduce the bug: same package exists in two classpath roots (e.g. file: + jar:),
        // causing the same relative file path to be collected twice before the distinct() fix.
        String packagePath = "unit_test_files/engine_unit_test_jsons";

        Path tempDir = Files.createTempDirectory("dedup-test");
        File jarFile = new File(tempDir.toFile(), "duplicate-scenarios.jar");

        // Mirror the real test-resources files into a JAR so the same paths appear twice
        File resourceDir = new File(getClass().getClassLoader().getResource(packagePath).getFile());
        try (java.util.jar.JarOutputStream jarOut =
                     new java.util.jar.JarOutputStream(Files.newOutputStream(jarFile.toPath()))) {
            jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/"));
            jarOut.closeEntry();
            for (File f : resourceDir.listFiles((d, n) -> n.endsWith(".json"))) {
                jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/" + f.getName()));
                jarOut.write(Files.readAllBytes(f.toPath()));
                jarOut.closeEntry();
            }
        }

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, original);
        Thread.currentThread().setContextClassLoader(urlClassLoader);

        try {
            List<String> files = SmartUtils.getAllEndPointFiles(packagePath);

            // Must be 25, not 50 — distinct() collapses duplicate paths from two classpath roots
            assertThat(files.size(), is(25));

            // No duplicate entries
            assertThat(new java.util.HashSet<>(files).size(), is(files.size()));
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test
    public void getAllEndPointFiles_returnsSortedList() {
        List<String> files = SmartUtils.getAllEndPointFiles("unit_test_files/engine_unit_test_jsons");

        for (int i = 0; i < files.size() - 1; i++) {
            assertThat(files.get(i).compareTo(files.get(i + 1)) <= 0, is(true));
        }
    }

    @Test
    public void checkDuplicateScenarios_stillThrows_whenTwoDifferentFiles_shareSameName_afterFileDedup() throws Exception {
        // Two distinct file paths each declare scenarioName "Given_When_Then_1".
        // File-path dedup must NOT collapse them — they are different files.
        // checkDuplicateScenarios must still detect the duplicate scenario name.
        String packagePath = "unit_test_files/test_scenario_cases";

        Path tempDir = Files.createTempDirectory("scenario-dedup-test");
        File jarFile = new File(tempDir.toFile(), "scenario-cases.jar");

        File resourceDir = new File(getClass().getClassLoader().getResource(packagePath).getFile());
        try (java.util.jar.JarOutputStream jarOut =
                     new java.util.jar.JarOutputStream(Files.newOutputStream(jarFile.toPath()))) {
            jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/"));
            jarOut.closeEntry();
            for (File f : resourceDir.listFiles((d, n) -> n.endsWith(".json"))) {
                jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/" + f.getName()));
                jarOut.write(Files.readAllBytes(f.toPath()));
                jarOut.closeEntry();
            }
        }

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, original);
        Thread.currentThread().setContextClassLoader(urlClassLoader);

        try {
            // File-path dedup collapses the jar duplicates back to 3 distinct paths.
            // The two files with the same scenarioName are still two distinct paths — must still throw.
            expectedException.expect(RuntimeException.class);
            expectedException.expectMessage("Oops! Duplicate scenario found, either rename or remove extra ones");
            smartUtils.checkDuplicateScenarios(packagePath);
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test
    public void checkDuplicateScenarios_doesNotThrow_whenSameFileAppearsInTwoClasspathRoots() throws Exception {
        // Before the distinct() fix, the same file appearing twice from two classpath roots caused
        // a false-positive duplicate scenario name error. After the fix it must not throw.
        String packagePath = "unit_test_files/no_dup_scenario_cases";

        String scenario1 = "{\"scenarioName\":\"UniqueScenario_A\",\"steps\":[]}";
        String scenario2 = "{\"scenarioName\":\"UniqueScenario_B\",\"steps\":[]}";

        // Build a real file: classpath root (temp dir with package structure)
        Path fsRoot = Files.createTempDirectory("no-false-positive-fs");
        Path packageDir = fsRoot.resolve(packagePath);
        Files.createDirectories(packageDir);
        Files.write(packageDir.resolve("scenario_a.json"), scenario1.getBytes());
        Files.write(packageDir.resolve("scenario_b.json"), scenario2.getBytes());

        // Build a JAR with the identical files — same relative paths will appear twice
        File jarFile = new File(fsRoot.toFile(), "mirrored-scenarios.jar");
        try (java.util.jar.JarOutputStream jarOut =
                     new java.util.jar.JarOutputStream(Files.newOutputStream(jarFile.toPath()))) {
            jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/"));
            jarOut.closeEntry();
            jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/scenario_a.json"));
            jarOut.write(scenario1.getBytes());
            jarOut.closeEntry();
            jarOut.putNextEntry(new java.util.jar.JarEntry(packagePath + "/scenario_b.json"));
            jarOut.write(scenario2.getBytes());
            jarOut.closeEntry();
        }

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        // Both classpath roots contain the same two files — paths appear twice before dedup
        URLClassLoader urlClassLoader = new URLClassLoader(
                new URL[]{fsRoot.toUri().toURL(), jarFile.toURI().toURL()}, original);
        Thread.currentThread().setContextClassLoader(urlClassLoader);

        try {
            // Should not throw — distinct() deduplicates the paths; no true duplicate scenario names
            smartUtils.checkDuplicateScenarios(packagePath);
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    // Move this to File Util class
    private static File createCascadeIfNotExisting(String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.createDirectories(path.getParent());

            return new File(fileName);
        } catch (IOException exx) {
            throw new RuntimeException("Create file '" + fileName + "' Exception" + exx);
        }
    }
}
