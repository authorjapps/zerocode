package org.jsmart.zerocode.testhelp.tests.helloworldtokenresolver;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldTokenResolverTest {

    @Test
    @JsonTestCase("helloworld_token_resolving/helloworld_token_resolving_ok.json")
    public void testHelloWorldTokenResolving_RESTApi() throws Exception {
    	
    }

}
