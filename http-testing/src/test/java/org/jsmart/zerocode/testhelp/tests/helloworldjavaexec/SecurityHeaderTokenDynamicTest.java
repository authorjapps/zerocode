package org.jsmart.zerocode.testhelp.tests.helloworldjavaexec;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class SecurityHeaderTokenDynamicTest {

    @Test
    @JsonTestCase("helloworldjavaexec/hello_world_security_token_for_header_test.json")
    public void testNewHeaderToken() throws Exception {

    }


}
