package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.CustomHelloHttpClient;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.HttpClient;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HttpClient(CustomHelloHttpClient.class)
@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class SmartJUnitNavigatorVerificationHttpClient {

    @JsonTestCase("01_verification_test_cases/01_get_more_bathroom_multi_steps.json")
    @Test
    public void testASmartTestCase_createUpdate() throws Exception {

    }
}

