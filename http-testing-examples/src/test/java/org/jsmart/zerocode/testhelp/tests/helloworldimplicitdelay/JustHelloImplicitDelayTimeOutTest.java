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
     * Server response delay = 2000 milli sec (2sec) - See the WireMock delay
     * Max timeout = 1000 milli sec (1 sec) - See the localhost_app.properties
     *  - The below test fails due to Max-timeout config
     *  - You can tweak this to different value and make it/Pass/Fail
     */
    @Test
    @Scenario("helloworld_implicit_delay/http_implicit_delay_max_timeout_scenario.json")
    public void testImplicitDelay_max1Sec() {
    }

}
