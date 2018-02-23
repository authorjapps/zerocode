package org.jsmart.zerocode.core.soap;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@UseHttpClient(SslTrustHttpClient.class) //Needed only for https connections
@TargetEnv("soap_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class SoapEndPointTest {

    @Ignore("This works. Ignored only to avoid build failure when the internet site is down")
    @Test
    @JsonTestCase("16_soap/soap_endpoint_soap_action_post_200.json")
    public void testSoapEndPointWith_SOAPAction() throws Exception {

    }
}

