package org.jsmart.zerocode.core.runner.e2e;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScenarioFileMissingAfterPassedScenario {

    @Test
    @JsonTestCase("integration_test_files/echo_step/01_echo_step_short_form.json")
    public void test1_passes() throws Exception {

    }

    @Test
    @JsonTestCase("integration_test_files/echo_step/this_scenario_file_does_not_exist.json")
    public void test2_scenarioFileMissing() throws Exception {

    }

    @Test
    @JsonTestCase("integration_test_files/echo_step/01_echo_step_short_form.json")
    public void test3_passes() throws Exception {

    }
}
