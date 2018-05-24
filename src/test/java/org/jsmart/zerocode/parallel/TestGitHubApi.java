package org.jsmart.zerocode.parallel;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class TestGitHubApi {

    @Test
    @JsonTestCase("load/github_get_api_sample_test.json")
    public void testGitHubApi_get() throws Exception {
    }
}
