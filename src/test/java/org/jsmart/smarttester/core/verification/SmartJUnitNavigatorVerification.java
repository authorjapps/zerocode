package org.jsmart.smarttester.core.verification;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.smarttester.core.domain.SmartTestCase;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.runner.SmartJUnitRunner;
import org.jsmart.smarttester.core.tests.customrunner.TestOnlySmartJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
//@RunWith(SmartJUnitRunner.class)
@RunWith(TestOnlySmartJUnitRunner.class)
public class SmartJUnitNavigatorVerification {

    @SmartTestCase("01_test_cases/01_REST_end_point_GET_PASS.json")
    @Test
    public void testASmartTestCase_PASSIfNoConnectionError() throws Exception {
    }

    @SmartTestCase("01_test_cases/02_java_service_single_step.json")
    @Test
    public void testASmartTestCase_Another() throws Exception {
    }

    @SmartTestCase("01_test_cases/999_file_not_there_.json")
    @Test
    public void testASmartTestCase_WrongFile() throws Exception {
    }

    @SmartTestCase("01_test_cases/01_sd_create_update.json")
    @Test
    public void testASmartTestCase_sdCreateUpdate() throws Exception {
    }

}
