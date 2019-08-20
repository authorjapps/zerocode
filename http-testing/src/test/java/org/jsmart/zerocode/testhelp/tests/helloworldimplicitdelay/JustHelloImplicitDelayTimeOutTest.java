package org.jsmart.zerocode.testhelp.tests.helloworldimplicitdelay;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.zerocodejavaexec.wiremock.ZeroCodeWireMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("localhost_app.properties")
@RunWith(ZeroCodeWireMockRunner.class)
public class JustHelloImplicitDelayTimeOutTest {

    /**
     *
     */
    @Test
    @Scenario("helloworld_implicit_delay/http_implicit_delay_max_timeout_scenario.json")
    public void testImplicitDelay_max1Sec() {
    }

}
