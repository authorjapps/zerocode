package org.jsmart.zerocode.github;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host_old_fashion_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class TestGitHubApiOldFashionHost {

    @Ignore("Locally passes, but fails in Travis CI due to rate limiting of GitHub")
    @Test
    @JsonTestCase("load/github_get_api_sample_test.json")
    public void testGitHubApi_get() throws Exception {
    }
}
