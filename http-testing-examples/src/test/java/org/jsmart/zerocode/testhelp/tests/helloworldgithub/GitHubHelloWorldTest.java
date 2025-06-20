package org.jsmart.zerocode.testhelp.tests.helloworldgithub;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class GitHubHelloWorldTest {

    @Test
    @JsonTestCase("helloworld_github_REST_api/GitHub_REST_api_sample_assertions.json")
    public void testGitGubSample_RESTApi() throws Exception {

    }

}
