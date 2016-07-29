package org.jsmart.zerocode.core.verify;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class SmartJUnitNavigatorVerification {

    @JsonTestCase("01_verification_test_cases/01_REST_end_point_GET_PASS.json")
    @Test
    public void testASmartTestCase_PASSIfNoConnectionError() throws Exception {
    }

    @JsonTestCase("01_verification_test_cases/02_java_service_single_step.json")
    @Test
    public void testASmartTestCase_Another() throws Exception {
    }

    @JsonTestCase("01_verification_test_cases/999_file_not_there_.json")
    @Test
    public void testASmartTestCase_WrongFile() throws Exception {
    }

    @JsonTestCase("01_verification_test_cases/01_get_more_bathroom_multi_steps.json")
    @Test
    public void testASmartTestCase_sdCreateUpdate() throws Exception {

    }

}
