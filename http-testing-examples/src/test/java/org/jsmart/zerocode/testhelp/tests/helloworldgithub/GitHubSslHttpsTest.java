package org.jsmart.zerocode.testhelp.tests.helloworldgithub;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.jsmart.zerocode.zerocodejavaexec.httpclient.CustomHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@UseHttpClient(CustomHttpClient.class)
@RunWith(ZeroCodeUnitRunner.class)
public class GitHubSslHttpsTest {

    @Test
    @JsonTestCase("helloworld_github_REST_api/GitHub_REST_api_sample_assertions.json")
    public void testGitGubSample_RESTApi() throws Exception {
    }

    // -------------------------------------------------------------------
    // This test has been added simply to check whether the same instance
    // of the custom Http client has been used to invoke the API
    // -------------------------------------------------------------------
    @Test
    @JsonTestCase("helloworld_github_REST_api/GitHub_REST_api_sample_assertions.json")
    public void testGitHub_GetAgain() throws Exception {
    }

}
