package org.jsmart.zerocode.testhelp.tests.helloworldretrywithsteps;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldRetryWithStepsTest {

    @Test
    @Scenario("helloworld_retry_withSteps/helloworld_retryWithSteps_test.json")
    public void retry_with_steps_test(){

    }
}
