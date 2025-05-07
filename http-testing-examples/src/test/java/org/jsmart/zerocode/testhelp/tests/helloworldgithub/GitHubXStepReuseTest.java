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
public class GitHubXStepReuseTest {

    @Test
    @JsonTestCase("helloworld_github_REST_api/GitHub_REST_api_step_reuse.json")
    public void gitHubRestApi_externalStepReuse() throws Exception {

    }

}
