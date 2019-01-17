package org.jsmart.zerocode.testhelp.tests.loadtesting.restendpoint;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="https://api.github.com", port=443, context = "")
@UseHttpClient(SslTrustHttpClient.class)
@RunWith(ZeroCodeUnitRunner.class)
public class TestGitGubEndPoint {

    @Test
    @JsonTestCase("loadtesting/github_get_api_test_case.json")
    public void testGitHubGET_load() throws Exception {

    }

}



