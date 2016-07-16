package org.jsmart.smarttester.core.verify;

import org.jsmart.smarttester.core.domain.JsonTestCase;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.runner.ZeroCodeTomcatUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(ZeroCodeTomcatUnitRunner.class)
public class ZeroCodeUnitTomcatNavigatorVerificationOther {

    @JsonTestCase("01_verification_test_cases/01_REST_end_point_GET_PASS.json")
    @Test
    public void testASmartTestCase_PASSIfNoConnectionError() throws Exception {

    }

    @JsonTestCase("01_verification_test_cases/02_java_service_single_step.json")
    @Test
    public void testASmartTestCase_Another() throws Exception {
    }

}
