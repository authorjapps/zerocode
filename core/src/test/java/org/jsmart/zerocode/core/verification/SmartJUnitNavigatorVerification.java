package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
//@RunWith(ZeroCodeUnitRunner.class)
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class SmartJUnitNavigatorVerification {

    @JsonTestCase("01_verification_test_cases/01_REST_end_point_GET_PASS.json")
    @Test
    public void testASmartTestCase_PASSIfNoConnectionError() throws Exception {

    }

    @JsonTestCase("01_verification_test_cases/999_file_not_there_.json")
    @Test
    public void testASmartTestCase_FileNotPresent() throws Exception {

    }

    @JsonTestCase("01_verification_test_cases/01_get_more_bathroom_multi_steps.json")
    @Test
    public void testASmartTestCase_createUpdate() throws Exception {

    }

    @JsonTestCase("11_reports/02_multi_scenario_multi_step_report_test.json")
    @Test
    public void willGeneratReport_multiSceneMultiSteps() throws Exception {

    }

    @JsonTestCase("01_verification_test_cases/02_java_service_single_step.json")
    @Test
    public void testJava_service_single_step() throws Exception {

    }
}
