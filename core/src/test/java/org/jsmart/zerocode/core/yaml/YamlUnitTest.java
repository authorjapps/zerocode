package org.jsmart.zerocode.core.yaml;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@TargetEnv("github_host_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class YamlUnitTest {

    @Test
    @JsonTestCase("unit_test_files/yaml/scenario_get_api_step_test.yml")
    public void testGitHubApi_get() {
    }
}
