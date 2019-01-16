package org.jsmart.zerocode.core.runner;

import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZeroCodePackageRunnerTest {

    ZeroCodePackageRunner zeroCodePackageRunner;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @TestPackageRoot("03_test_one_multi_steps")
    public static class FlowSpecExampleTest {
    }

    @Before
    public void initializeRunner() throws Exception {
        zeroCodePackageRunner = new ZeroCodePackageRunner(FlowSpecExampleTest.class);
    }

    @Test
    public void willHaveListOf_TestCases_Here() throws Exception {
        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void willHaveListOf_TestCases_Frompackage() throws Exception {
        zeroCodePackageRunner = new ZeroCodePackageRunner(FlowExamplePackagePickerClass.class);
        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void willComplain_If_Annotation_Missing() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Ah! Almost there. Just missing root package details");
        zeroCodePackageRunner = new ZeroCodePackageRunner(FlowExampleWithoutAnnotationClass.class);
        zeroCodePackageRunner.getChildren();
    }

    @Test
    public void testCanDescribeAChild_oldFashined() throws Exception {
        zeroCodePackageRunner = new ZeroCodePackageRunner(FlowExamplePackagePickerClass.class);

        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        Description childDescription = zeroCodePackageRunner.describeChild(children.get(0));

        assertThat(childDescription.getDisplayName(), containsString("Given_When_Then-Flow name"));

    }

    @Test
    @Ignore
    public void testCanDescribeAChild_RightClick_And_Runnable() throws Exception {

    }

    @Test
    public void testWillFireASingleStep_Child() throws Exception {
        //Injection done
        zeroCodePackageRunner = new ZeroCodePackageRunner(FlowExamplePackagePickerClass.class);

        // Now prepare the steps as if they were run via junit
        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        zeroCodePackageRunner.describeChild(children.get(0));
        RunNotifier notifier = new RunNotifier();
        zeroCodePackageRunner.runChild(children.get(0), notifier);

        //assertion sections
        assertThat(zeroCodePackageRunner.testRunCompleted, is(true));
        assertThat(zeroCodePackageRunner.isPassed(), is(false)); //<--- Not necessary to test as this can change dependeing on test
    }

    @Test
    public void testWillResolve_PlaceHolders_InASingleStep_Child() throws Exception {
        //Injection done
        zeroCodePackageRunner = new ZeroCodePackageRunner(MultiStepWithPlaceHolderTestClass.class);

        // Now prepare the steps as if they were run via junit
        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        zeroCodePackageRunner.describeChild(children.get(0));
        RunNotifier notifier = new RunNotifier();
        zeroCodePackageRunner.runChild(children.get(0), notifier);

        //assertion sections
        assertThat(zeroCodePackageRunner.testRunCompleted, is(true));
    }

    @TestPackageRoot("06_test_with_place_holders")
    public class MultiStepWithPlaceHolderTestClass {
    }
}
