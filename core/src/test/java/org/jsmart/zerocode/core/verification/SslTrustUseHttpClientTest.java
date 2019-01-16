package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@UseHttpClient(SslTrustHttpClient.class)
@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class SslTrustUseHttpClientTest {

    @Test
    @JsonTestCase("01_verification_test_cases/21_ssl_trust.json")
    public void testASmartTestCase_createUpdate() throws Exception {

    }
}

