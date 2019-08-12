package org.jsmart.zerocode.core.domain.reports;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeRunTwoScenariosForReport {
    
    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @JsonTestCase("integration_test_files/reports_e2e/two_step_scenario_1.json")
    public void test_scene1() throws Exception {
    }

    @Test
    @JsonTestCase("integration_test_files/reports_e2e/two_step_scenario_2.json")
    public void test_scene2() throws Exception {
    }

}



