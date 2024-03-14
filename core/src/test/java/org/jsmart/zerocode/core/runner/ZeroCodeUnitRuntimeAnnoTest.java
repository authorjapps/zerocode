package org.jsmart.zerocode.core.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * This just POC. But look for full test coverage in CLI module(TODO - when done)
 * Test names:
 *   - AnnotationUtilsTest.java
 *   - CliMainTest.java
 */
public class ZeroCodeUnitRuntimeAnnoTest {

    ZeroCodeUnitRunner zeroCodeUnitRunner;

    private static final String ANNOTATION_METHOD = "annotationData";
    private static final String ANNOTATION_FIELDS = "declaredAnnotations";
    private static final String ANNOTATIONS = "annotations";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @TargetEnv("github_host.properties")
    @RunWith(ZeroCodePackageRunner.class)
    @TestPackageRoot("foo_folder")
    public static class TinyPackageSuite {
    }

    @TargetEnv("foo_host.properties")
    public static class TinyScenarioTest {

        @JsonTestCase("/abcd/path")
        @Test
        public void testScenario1() throws Exception {
        }

    }

    public static void alterAnnotation(Class<?> targetClass, Class<? extends Annotation> targetAnnotation, Annotation targetValue) {
        try {
            Method method = Class.class.getDeclaredMethod(ANNOTATION_METHOD, (Class<?>[]) null);
            method.setAccessible(true);

            Object annotationData = method.invoke(targetClass);

            Field annotations = annotationData.getClass().getDeclaredField(ANNOTATIONS);
            annotations.setAccessible(true);

            Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
            map.put(targetAnnotation, targetValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Ignore("need helloworld folder to be present in the src/main")
    @Test
    public void testRun_junitCore() {
        TestPackageRoot testFolder = new TestPackageRoot() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TestPackageRoot.class;
            }

            @Override
            public String value() {
                return "helloworld";
            }
        };

        TargetEnv hostProperties = new TargetEnv() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TargetEnv.class;
            }

            @Override
            public String value() {
                return "github_host.properties";
            }
        };

        alterAnnotation(TinyPackageSuite.class, TestPackageRoot.class, testFolder);
        alterAnnotation(TinyPackageSuite.class, TargetEnv.class, hostProperties);

        Result result = JUnitCore.runClasses(TinyPackageSuite.class);
        assertThat(result.getRunCount(), CoreMatchers.is(1));
    }

    @Test
    public void testAnnoRuntime_rootPackage() {

        TestPackageRoot oldAnnotation = TinyPackageSuite.class.getAnnotationsByType(TestPackageRoot.class)[0];
        System.out.println("oldAnnotation = " + oldAnnotation.value());

        TestPackageRoot testFolder = new TestPackageRoot() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TestPackageRoot.class;
            }

            @Override
            public String value() {
                return "helloworld";
            }
        };

        alterAnnotation(TinyPackageSuite.class, TestPackageRoot.class, testFolder);

        TestPackageRoot newAnno = TinyPackageSuite.class.getAnnotationsByType(TestPackageRoot.class)[0];
        assertThat(newAnno.value(), is("helloworld"));
    }

    @Test
    public void testAnnoRuntime_targetEnv() {

        TargetEnv oldAnnotation = TinyScenarioTest.class.getAnnotationsByType(TargetEnv.class)[0];
        System.out.println("oldAnnotation = " + oldAnnotation.value());

        TargetEnv hostProperties = new TargetEnv() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return TargetEnv.class;
            }

            @Override
            public String value() {
                return "new_host.properties";
            }
        };

        alterAnnotation(TinyScenarioTest.class, TargetEnv.class, hostProperties);

        TargetEnv newAnno = TinyScenarioTest.class.getAnnotationsByType(TargetEnv.class)[0];
        assertThat(newAnno.value(), is("new_host.properties"));
    }

    @Test
    public void testAnnoRuntime_scenarioAnno() throws NoSuchMethodException {


        JsonTestCase newAnnotation = new JsonTestCase() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonTestCase.class;
            }

            @Override
            public String value() {
                return "/path/to/scenario.json";
            }
        };

        alterAnnotation(TinyScenarioTest.class, JsonTestCase.class, newAnnotation);

        JsonTestCase newAnno = TinyScenarioTest.class.getAnnotationsByType(JsonTestCase.class)[0];
        assertThat(newAnno.value(), is("/path/to/scenario.json"));

    }

    @Test
    public void testAnnoRuntime_scenarioAnnoMethod() throws NoSuchMethodException {


        JsonTestCase newAnnotation = new JsonTestCase() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonTestCase.class;
            }

            @Override
            public String value() {
                return "/path/to/scenario.json";
            }
        };

        alterAnnotationValueField(TinyScenarioTest.class, JsonTestCase.class, newAnnotation);

        JsonTestCase newAnno2 = TinyScenarioTest.class.getDeclaredMethods()[0].getAnnotationsByType(JsonTestCase.class)[0];
        assertThat(newAnno2.value(), is("/path/to/scenario.json"));
        //method.getMethod().getAnnotation(JsonTestCase.class);
    }

    public static void alterAnnotationValueField(Class<?> targetClass, Class<? extends Annotation> targetAnnotation, Annotation targetValue) {
        try {
            Method method = targetClass.getDeclaredMethod("testScenario1");
            method.setAccessible(true);

            method.getDeclaredAnnotations();
            Class<?> superclass = method.getClass().getSuperclass();
            Field declaredField = superclass.getDeclaredField("declaredAnnotations");
            declaredField.setAccessible(true);
            Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) declaredField
                    .get(method);
            map.put(targetAnnotation, targetValue);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}