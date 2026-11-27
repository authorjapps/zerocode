package org.jsmart.zerocode.core.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jsmart.zerocode.core.runner.e2e.ScenarioFileMissingAfterPassedScenario;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ZeroCodeUnitRunner_ScenarioFileMissingTest {

    @Test
    public void test_scenarioFileMissingAfterPassedScenario_failsOnlyThatTestAndRunsTheRest() {

        Result result = JUnitCore.runClasses(ScenarioFileMissingAfterPassedScenario.class);

        assertThat(result.getRunCount(), is(3));
        assertThat(result.getFailureCount(), is(1));

        Failure failure = result.getFailures().get(0);
        assertThat(failure.getDescription().getMethodName(), is("test2_scenarioFileMissing"));
        assertThat(failure.getMessage(), containsString("this_scenario_file_does_not_exist.json"));
    }
}
