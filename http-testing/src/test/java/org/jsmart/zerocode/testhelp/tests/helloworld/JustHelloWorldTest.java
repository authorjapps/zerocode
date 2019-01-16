package org.jsmart.zerocode.testhelp.tests.helloworld;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldTest {

    @Test
    @JsonTestCase("helloworld/hello_world_status_ok_assertions.json")
    public void testGet() throws Exception {
    }

}
