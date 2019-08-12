package org.jsmart.zerocode.core.runner;

import java.util.List;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.JsonTestCases;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZeroCodePackageRunnerSelectedTest {

    ZeroCodePackageRunner zeroCodePackageRunner;

    @JsonTestCases({
            @JsonTestCase("unit_test_files/cherry_pick_tests/folder_a/test_case_1.json"), //any valid path
            @JsonTestCase("unit_test_files/cherry_pick_tests/folder_b/test_case_2.json"), //any valid path
    })
    public static class ScenarioTestFlowExampleSelectedTest {
    }

    @TestPackageRoot("unit_test_files/test_scenario_multi_steps")
    public static class ScenarioTestFlowSelectedExampleTest {
    }

    @Before
    public void initializeRunner() throws Exception {
        zeroCodePackageRunner = new ZeroCodePackageRunner(ScenarioTestFlowExampleSelectedTest.class);
    }

    @Test
    public void willHaveListOf_TestCases_Here() throws Exception {
        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        assertThat(children.size(), is(2));
    }

    @Test
    public void testDescribeAChild() throws Exception {
        List<ScenarioSpec> children = zeroCodePackageRunner.getChildren();
        Description childDescription = zeroCodePackageRunner.describeChild(children.get(0));

        assertThat(childDescription.getDisplayName(), containsString("test scenario 1"));
    }

}
