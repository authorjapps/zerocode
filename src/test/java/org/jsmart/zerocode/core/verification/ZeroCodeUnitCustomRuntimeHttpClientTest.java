package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.httpclient.CustomRuntimeTestHttpClient;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.HttpClient;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HttpClient(CustomRuntimeTestHttpClient.class)
@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeUnitCustomRuntimeHttpClientTest {

    @Test
    @JsonTestCase("01_verification_test_cases/04_custom_runtime_http_client.json")
    public void testASmartTestCase_createUpdate() throws Exception {

    }
}

