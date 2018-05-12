package org.jsmart.zerocode.core.verification.loopreport;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class SmartJUnitNavigatorReportGenFailureStep {

    @Test
    @JsonTestCase("11_reports/02.2_loop_scenario_only_one_failing_step.json")
    public void willGeneratReport_multiSceneMultiSteps() throws Exception {

    }
}
