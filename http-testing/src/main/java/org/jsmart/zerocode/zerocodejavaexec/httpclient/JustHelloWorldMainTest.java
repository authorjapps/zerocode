package org.jsmart.zerocode.zerocodejavaexec.httpclient;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldMainTest {

    @Test
    @Scenario("helloworld/hello_world_status_ok_assertions.json")
    public void testGet() throws Exception {
    }

}
