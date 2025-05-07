package org.jsmart.zerocode.testhelp.tests.helloworldoneof;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldOneOfTest {

    @Test
    @JsonTestCase("helloworld_one_of/hello_world_one_of_test.json")
    public void testValueOneOf() throws Exception {

    }
}
