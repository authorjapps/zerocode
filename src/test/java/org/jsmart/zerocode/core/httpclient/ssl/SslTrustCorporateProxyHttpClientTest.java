package org.jsmart.zerocode.core.httpclient.ssl;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@UseHttpClient(SslTrustCorporateProxyHttpClient.class)
@TargetEnv("soap_host_with_corp_proxy.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class SslTrustCorporateProxyHttpClientTest {

    @Ignore("this is only a usage example using dummy proxy details")
    @Test
    @JsonTestCase("01_verification_test_cases/21_ssl_trust.json")
    public void testASmartTestCase_createUpdate() throws Exception {

    }
}