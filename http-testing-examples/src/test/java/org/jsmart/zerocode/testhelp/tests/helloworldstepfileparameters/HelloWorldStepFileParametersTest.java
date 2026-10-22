package org.jsmart.zerocode.testhelp.tests.helloworldstepfileparameters;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.jsmart.zerocode.testhelp.localserver.RunMeFirstLocalMockRESTServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Shows how the same "stepFiles" is reused twice in one scenario with different "parameters",
 * read inside the step file as ${PARAM.username} and ${PARAM.role}.
 * <p>
 * Starts and stops the local mock REST server itself, hence needs no manual setup.
 */
@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldStepFileParametersTest {

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
    @Scenario("helloworld_step_file_parameters/create_users_with_step_file_parameters_test.json")
    public void testSameStepFileWithDifferentParametersWillCreateAndVerifyEachUser() throws Exception {
    }
}
