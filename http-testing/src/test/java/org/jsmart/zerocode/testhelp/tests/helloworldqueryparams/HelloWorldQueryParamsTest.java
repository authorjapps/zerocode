package org.jsmart.zerocode.testhelp.tests.helloworldqueryparams;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldQueryParamsTest {

    @Test
    @JsonTestCase("helloworld_queryparams/github_get_repos_by_query_params.json")
    public void testGetBy_queryParams() throws Exception {
    }

}
