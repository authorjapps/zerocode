package org.jsmart.zerocode.core.runner.parallel;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.parallel.restful.JunitRestTestSample;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ZeroCodeLoadRunnerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    ZeroCodeLoadRunner zeroCodePackageRunner;
    ZeroCodeLoadRunner runnerNoTestMapping;
    ZeroCodeLoadRunner runnerMultiTestMapping;
    ZeroCodeLoadRunner runnerInvalidTestMapping;

    @LoadWith("load_config_test.properties")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
    @RunWith(ZeroCodeLoadRunner.class)
    public class ExampleRunnerTest { //note- "public" is necessary

    }

    // @TestMapping not present to check exception
    @LoadWith("load_config_test.properties")
    @RunWith(ZeroCodeLoadRunner.class)
    public class ExampleRunnerWithoutMappingTest {
    }

    @LoadWith("load_config_test.properties")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_fail")
    @RunWith(ZeroCodeLoadRunner.class)
    public class ExampleRunnerMultiMappingTest {
    }

    @LoadWith("load_config_test.properties")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "dodgyAndInvalidTestMethod")
    @RunWith(ZeroCodeLoadRunner.class)
    public class ExampleRunnerInvalidMethodTest {
    }

    @Before
    public void initializeRunner() throws Exception {
        zeroCodePackageRunner = new ZeroCodeLoadRunner(ExampleRunnerTest.class);
        runnerNoTestMapping = new ZeroCodeLoadRunner(ExampleRunnerWithoutMappingTest.class);
        runnerMultiTestMapping = new ZeroCodeLoadRunner(ExampleRunnerMultiMappingTest.class);
        runnerInvalidTestMapping = new ZeroCodeLoadRunner(ExampleRunnerInvalidMethodTest.class);
    }

    @Test
    public void testPair_annotaionSingle() {
        TestMapping mapping = ExampleRunnerTest.class.getAnnotation(TestMapping.class);
        assertThat(mapping.testMethod(), is("testGetCallToHome_pass"));
        assertThat(mapping.testClass().getName(), is((JunitRestTestSample.class.getName())));
    }

    @Test
    public void test_noMapping() {
        expectedException.expectMessage("Ah! You missed to put the @TestMapping on the load-generating test class");
        List<TestMapping> children = runnerNoTestMapping.getChildren();
    }

    @Test
    public void testPair_multiMappingException() {
        expectedException.expectMessage("Oops! Needs single @TestMapping, but found multiple of it on the load-generating test class");
        List<TestMapping> children = runnerMultiTestMapping.getChildren();
    }

    @Test
    public void testPair_invalidTestMethodMappingException() {
        expectedException.expectMessage("Mapped test method `dodgyAndInvalidTestMethod` was invalid, please re-check and pick");
        List<TestMapping> children = runnerInvalidTestMapping.getChildren();
    }

    @Test
    public void testPair_size() {
        List<TestMapping> children = zeroCodePackageRunner.getChildren();
        assertThat(children.size(), is(1));
    }

    @Test
    public void testPair_singleClassVsMethodMap() {
        List<TestMapping> children = zeroCodePackageRunner.getChildren();
        assertThat(children.get(0).testMethod(), is("testGetCallToHome_pass"));
        assertThat(children.get(0).testClass().getName(), is((JunitRestTestSample.class.getName())));
    }

    @Test
    public void testLoad_properties() {
        LoadWith loadWithFile = ExampleRunnerTest.class.getAnnotation(LoadWith.class);
        assertThat(loadWithFile.value(), is("load_config_test.properties"));
    }

}