package org.jsmart.zerocode.testhelp.tests.helloworldgithub;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@UseHttpClient(SslTrustHttpClient.class)
@RunWith(ZeroCodeUnitRunner.class)
public class MoreGitHubSslAndAssertionsTest {

    @Test
    @JsonTestCase("helloworld_github_REST_api/GitHub_REST_api_more_assertions.json")
    public void testGitGubVersionRESTApiWith_easyAssertions() throws Exception {

    }

}
