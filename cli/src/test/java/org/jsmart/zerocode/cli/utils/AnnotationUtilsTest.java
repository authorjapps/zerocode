package org.jsmart.zerocode.cli.utils;

import java.lang.annotation.Annotation;
import org.hamcrest.CoreMatchers;
import org.jsmart.zerocode.cli.precli.ZeroCodeUnitRunnerTest;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.cli.utils.AnnotationUtils.alterClassAnnotation;
import static org.jsmart.zerocode.cli.utils.AnnotationUtils.alterMethodAnnotation;

public class AnnotationUtilsTest {

    ZeroCodeUnitRunner zeroCodeUnitRunner;

    private static final String ANNOTATION_METHOD = "annotationData";
    private static final String ANNOTATION_FIELDS = "declaredAnnotations";
    private static final String ANNOTATIONS = "annotations";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // ----------------
    // A package picker
    // ----------------
    @TargetEnv("foo_host.properties")
    @RunWith(ZeroCodePackageRunner.class)
    @TestPackageRoot("foo_folder")
    public static class TinyPackageSuite {

    }

    // --------------------
    // A single test picker
    // --------------------
    @TargetEnv("foo_host.properties")
    public static class TinyScenarioTest {

        @JsonTestCase("/abcd/path")
        @Test
        public void testScenario1() throws Exception {
        }
    }

    @Test
    public void testRunVia_junitCore() {

        TestPackageRoot testFolder = createTestPackageRoot("my_tests");
        TargetEnv hostProperties = createTargetEnv("my_host.properties");

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TestPackageRoot.class, testFolder);
        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TargetEnv.class, hostProperties);

        Result result = JUnitCore.runClasses(ZeroCodeUnitRunnerTest.TinyPackageSuite.class);
        assertThat(result.getRunCount(), CoreMatchers.is(1));
        assertThat(result.getFailures().size(), CoreMatchers.is(0));
    }

    @Test
    public void testRunVia_junitCore_fail() {

        TargetEnv hostProperties = createTargetEnv("my_host_YY.properties"); //<-- bad file name
        TestPackageRoot testFolder = createTestPackageRoot("my_tests"); //<-- bad folder

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TestPackageRoot.class, testFolder);
        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TargetEnv.class, hostProperties);

        Result result = JUnitCore.runClasses(ZeroCodeUnitRunnerTest.TinyPackageSuite.class);
        assertThat(result.getRunCount(), CoreMatchers.is(1));
        assertThat(result.getFailures().size(), CoreMatchers.is(1));
    }

    @Test
    public void testRunVia_junitCore_fail_badFolder() {

        TestPackageRoot testFolder = createTestPackageRoot("my_tests_XX"); //<-- bad folder
        TargetEnv hostProperties = createTargetEnv("my_host.properties"); //<-- bad file name

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TestPackageRoot.class, testFolder);
        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TargetEnv.class, hostProperties);

        expectedException.expectMessage("NothingFoundHereException: Check the (my_tests_XX) integration test repo folder(empty?).");
        Result result = JUnitCore.runClasses(ZeroCodeUnitRunnerTest.TinyPackageSuite.class);
    }


    @Test
    public void testUpdateToNewVal_rootPackage() {

        TestPackageRoot oldAnnotation = ZeroCodeUnitRunnerTest.TinyPackageSuite.class.getAnnotationsByType(TestPackageRoot.class)[0];
        assertThat(oldAnnotation.value(), is("foo_folder"));

        TestPackageRoot testFolder = createTestPackageRoot("new_folder_x");

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyPackageSuite.class, TestPackageRoot.class, testFolder);

        TestPackageRoot newAnno = ZeroCodeUnitRunnerTest.TinyPackageSuite.class.getAnnotationsByType(TestPackageRoot.class)[0];
        assertThat(newAnno.value(), is("new_folder_x"));
    }

    @Test
    public void testUpdateToNewVal_targetEnv() {

        TargetEnv oldAnnotation = ZeroCodeUnitRunnerTest.TinyScenarioTest.class.getAnnotationsByType(TargetEnv.class)[0];
        assertThat(oldAnnotation.value(), is("foo_host.properties"));

        TargetEnv hostProperties = createTargetEnv("new_host.properties");

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyScenarioTest.class, TargetEnv.class, hostProperties);

        TargetEnv newAnno = ZeroCodeUnitRunnerTest.TinyScenarioTest.class.getAnnotationsByType(TargetEnv.class)[0];
        assertThat(newAnno.value(), is("new_host.properties"));
    }

    @Test
    public void testAnnoRuntime_scenarioAnno() throws NoSuchMethodException {
        Scenario newAnnotation = createScenario("/path/to/scenario.json");

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyScenarioTest.class, Scenario.class, newAnnotation);

        Scenario newAnno = ZeroCodeUnitRunnerTest.TinyScenarioTest.class.getAnnotationsByType(Scenario.class)[0];
        assertThat(newAnno.value(), is("/path/to/scenario.json"));
    }

    @Test
    public void testAnnoRuntime_scenarioAnnoMethod() throws NoSuchMethodException {
        Scenario newAnnotation = createScenario("/path/to/scenario.json");

        alterMethodAnnotation(ZeroCodeUnitRunnerTest.TinyScenarioTest.class, Scenario.class, newAnnotation, "testScenario1");

        Scenario newAnno2 = ZeroCodeUnitRunnerTest.TinyScenarioTest.class.getDeclaredMethods()[0].getAnnotationsByType(Scenario.class)[0];
        assertThat(newAnno2.value(), is("/path/to/scenario.json"));
    }

    @Test
    public void testAnnoRuntimeBoth_classNmethod() throws NoSuchMethodException {
        TargetEnv hostProps = createTargetEnv("new_folder_y");
        Scenario newAnnotation = createScenario("/path/to/scenario.json");

        alterClassAnnotation(ZeroCodeUnitRunnerTest.TinyScenarioTest.class, TargetEnv.class, hostProps);
        alterMethodAnnotation(ZeroCodeUnitRunnerTest.TinyScenarioTest.class, Scenario.class, newAnnotation, "testScenario1");

        TargetEnv methodAnno = ZeroCodeUnitRunnerTest.TinyScenarioTest.class.getAnnotationsByType(TargetEnv.class)[0];
        assertThat(methodAnno.value(), is("new_folder_y"));

        Scenario classAnno = ZeroCodeUnitRunnerTest.TinyScenarioTest.class.getDeclaredMethods()[0].getAnnotationsByType(Scenario.class)[0];
        assertThat(classAnno.value(), is("/path/to/scenario.json"));

    }

    private TestPackageRoot createTestPackageRoot(String useTestFolder) {
        return new TestPackageRoot() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return TestPackageRoot.class;
            }

            @Override
            public String value() {
                return useTestFolder;
            }
        };
    }

    private TargetEnv createTargetEnv(String hostProperties) {
        return new TargetEnv() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return TargetEnv.class;
            }

            @Override
            public String value() {
                return hostProperties;
            }
        };

    }

    private Scenario createScenario(String scenarioPath) {

        return new Scenario() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Scenario.class;
            }

            @Override
            public String value() {
                return scenarioPath;
            }
        };

    }


}