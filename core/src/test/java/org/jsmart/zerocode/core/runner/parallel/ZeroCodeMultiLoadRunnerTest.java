package org.jsmart.zerocode.core.runner.parallel;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.parallel.restful.JunitRestTestSample;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ZeroCodeMultiLoadRunnerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    ZeroCodeMultiLoadRunner zeroCodePackageGroupRunner;
    ZeroCodeMultiLoadRunner zeroCodePackageRunner;
    ZeroCodeMultiLoadRunner runnerNoTestMapping;
    ZeroCodeMultiLoadRunner runnerMultiTestMapping;
    ZeroCodeMultiLoadRunner runnerInvalidTestMapping;

    @LoadWith("load_config_test.properties")
    @TestMappings({
            @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass"),
            @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_fail"),
    })
    @RunWith(ZeroCodeMultiLoadRunner.class)
    public class ExampleRunnerWithGroupTest { //note "public" is necessary

    }

    @LoadWith("load_config_test.properties")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
    @RunWith(ZeroCodeMultiLoadRunner.class)
    public class ExampleRunnerTest {

    }

    // @TestMapping not present to check exception
    @LoadWith("load_config_test.properties")
    @RunWith(ZeroCodeMultiLoadRunner.class)
    public class ExampleRunnerWithoutMappingTest {
    }

    @LoadWith("load_config_test.properties")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_pass")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "testGetCallToHome_fail")
    @RunWith(ZeroCodeMultiLoadRunner.class)
    public class ExampleRunnerMultiMappingTest {
    }

    @LoadWith("load_config_test.properties")
    @TestMapping(testClass = JunitRestTestSample.class, testMethod = "dodgyAndInvalidTestMethod")
    @RunWith(ZeroCodeMultiLoadRunner.class)
    public class ExampleRunnerInvalidMethodTest {
    }

    @Before
    public void initializeRunner() throws Exception {
        zeroCodePackageGroupRunner = new ZeroCodeMultiLoadRunner(ExampleRunnerWithGroupTest.class);
        zeroCodePackageRunner = new ZeroCodeMultiLoadRunner(ExampleRunnerTest.class);
        runnerNoTestMapping = new ZeroCodeMultiLoadRunner(ExampleRunnerWithoutMappingTest.class);
        runnerMultiTestMapping = new ZeroCodeMultiLoadRunner(ExampleRunnerMultiMappingTest.class);
        runnerInvalidTestMapping = new ZeroCodeMultiLoadRunner(ExampleRunnerInvalidMethodTest.class);
    }

    @Test
    public void testPair_sizeTwo() {
        List<TestMapping[]> children = zeroCodePackageGroupRunner.getChildren();
        assertThat(children.size(), is(1));
        assertThat(children.get(0).length, is(2));
    }

    @Test
    public void testPair_multiMappingWithoutGrouping() {
        List<TestMapping[]> children = runnerMultiTestMapping.getChildren();
        assertThat(children.size(), is(1));
        assertThat(children.get(0).length, is(2));
    }

    @Test
    public void testPair_annotaionSingle() {
        TestMapping mapping = ExampleRunnerTest.class.getAnnotation(TestMapping.class);
        assertThat(mapping.testMethod(), is("testGetCallToHome_pass"));
        assertThat(mapping.testClass().getName(), is((JunitRestTestSample.class.getName())));
    }

    @Test
    public void testPair_size() {
        List<TestMapping[]> children = zeroCodePackageRunner.getChildren();
        assertThat(children.size(), is(1));
    }

    @Test
    public void testPair_singleClassVsMethodMap() {
        List<TestMapping[]> children = zeroCodePackageRunner.getChildren();
        assertThat(children.get(0)[0].testMethod(), is("testGetCallToHome_pass"));
        assertThat(children.get(0)[0].testClass().getName(), is((JunitRestTestSample.class.getName())));
    }

    @Test
    public void testLoad_properties() {
        LoadWith loadWithFile = ExampleRunnerTest.class.getAnnotation(LoadWith.class);
        assertThat(loadWithFile.value(), is("load_config_test.properties"));
    }

    @Test
    public void test_noMappingException() {
        expectedException.expectMessage("Ah! You missed to put the @TestMapping on the load-generating test class");
        List<TestMapping[]> children = runnerNoTestMapping.getChildren();
    }

    @Test
    public void testPair_invalidTestMethodMappingException() {
        expectedException.expectMessage("Mapped test method `dodgyAndInvalidTestMethod` was invalid, please re-check and pick");
        List<TestMapping[]> children = runnerInvalidTestMapping.getChildren();
    }
}