package org.jsmart.smarttester.core.runner;

import com.google.inject.Inject;
import org.jsmart.smarttester.core.di.SmartServiceModule;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JukitoRunner.class)
@UseModules(SmartServiceModule.class)
public class SmartRunnerTest {

    @Inject
    SmartUtils smartUtils;

    @Inject
    MultiStepsRunner multiStepsRunner;

    SmartRunner smartRunner;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @TestPackageRoot("test_one_multi_steps")
    public static class FlowSpecExampleTest {
    }

    @Before
    public void initializeRunner() throws Exception {
        smartRunner = new SmartRunner(FlowSpecExampleTest.class, smartUtils);
    }

    @Test
    public void willHaveListOf_TestCases_Here() throws Exception {
        List<FlowSpec> children = smartRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void willHaveListOf_TestCases_Frompackage() throws Exception {
        smartRunner = new SmartRunner(FlowExamplePackagePickerClass.class, smartUtils);
        List<FlowSpec> children = smartRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void willComplain_If_Annotation_Missing() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Ah! Almost there. Just missing root package details");
        smartRunner = new SmartRunner(FlowExampleWithoutAnnotationClass.class, smartUtils);
        smartRunner.getChildren();
    }

    @Test
    public void testCanDescribeAChild_oldFashined() throws Exception {
        smartRunner = new SmartRunner(FlowExamplePackagePickerClass.class);
        smartRunner.setSmartUtils(smartUtils);

        List<FlowSpec> children = smartRunner.getChildren();
        Description childDescription = smartRunner.describeChild(children.get(0));

        assertThat(childDescription.getDisplayName(), containsString("Given_When_Then-Flow name"));

    }

    @Test
    @Ignore
    public void testCanDescribeAChild_RightClick_And_Runnable() throws Exception {

    }

    @Test
    public void testWillFireASingleStep_Child() throws Exception {
        //Injection over
        smartRunner = new SmartRunner(FlowExamplePackagePickerClass.class, smartUtils);
        smartRunner.setMultiStepsRunner(multiStepsRunner);

        // Now prepare the steps as if they were run via junit
        List<FlowSpec> children = smartRunner.getChildren();
        smartRunner.describeChild(children.get(0));

        RunNotifier notifier = new RunNotifier();
        smartRunner.runChild(children.get(0), notifier);

        //assertThat(smartRunner.isRunSuccess(), is(true));
        assertThat(smartRunner.isPassed(), is(true));
    }
}
