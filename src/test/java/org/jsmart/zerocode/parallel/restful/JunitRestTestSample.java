package org.jsmart.zerocode.parallel.restful;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@UseHttpClient(SslTrustHttpClient.class)
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class JunitRestTestSample {

    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @JsonTestCase("load/simple_load_at_localhost.json")
    public void testGetCallToHome_pass() throws Exception {

    }

    @Test
    @JsonTestCase("load/simple_load_at_localhost_fail.json")
    public void testGetCallToHome_fail() throws Exception {

    }


}



