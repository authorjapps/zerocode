package org.jsmart.zerocode.github;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class TestGitHubApi {

    @Ignore("Locally passes, but fails in Travis CI due to rate limiting issue of GitHub")
    @Test
    @JsonTestCase("load/github_get_api_sample_test.json")
    public void testGitHubApi_get() throws Exception {
    }
}
