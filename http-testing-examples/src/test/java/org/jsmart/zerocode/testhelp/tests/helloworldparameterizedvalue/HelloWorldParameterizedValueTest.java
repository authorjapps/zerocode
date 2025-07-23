package org.jsmart.zerocode.testhelp.tests.helloworldparameterizedvalue;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldParameterizedValueTest {

    @Test
    @Scenario("parameterized_value/hello_world_test_parameterized_value.json")
    public void testGetByUserNames() throws Exception {
    }

}
