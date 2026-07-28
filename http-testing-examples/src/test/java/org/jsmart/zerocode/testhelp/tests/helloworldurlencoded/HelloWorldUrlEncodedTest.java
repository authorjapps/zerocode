package org.jsmart.zerocode.testhelp.tests.helloworldurlencoded;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.jsmart.zerocode.testhelp.localserver.RunMeFirstLocalMockRESTServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Shows how an id containing special characters, e.g. "#37:29", is url encoded to
 * "%2337%3A29" before it is used in the url of the next step.
 * <p>
 * Starts and stops the local mock REST server itself, hence needs no manual setup.
 */
@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldUrlEncodedTest {

    private static RunMeFirstLocalMockRESTServer mockRESTServer;

    @BeforeClass
    public static void startMockServer() {
        mockRESTServer = new RunMeFirstLocalMockRESTServer(RunMeFirstLocalMockRESTServer.PORT);
        mockRESTServer.start();
    }

    @AfterClass
    public static void stopMockServer() {
        mockRESTServer.stop();
    }

    @Test
    @Scenario("helloworld_url_encoded/url_encoded_id_in_next_step_test.json")
    public void testUrlEncodedIdOfEarlierStep() throws Exception {
    }
}
