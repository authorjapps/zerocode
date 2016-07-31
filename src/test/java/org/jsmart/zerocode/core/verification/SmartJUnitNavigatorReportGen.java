package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class SmartJUnitNavigatorReportGen {

    @Test
    @JsonTestCase("11_reports/02_multi_scenario_multi_step_report_test.json")
    public void willGeneratReport_multiSceneMultiSteps() throws Exception {

    }
}
