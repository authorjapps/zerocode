package org.jsmart.zerocode.testhelp.tests.helloworldnamedcsvparam;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldNamedCsvParamsTest {

    @Test
    @Scenario("parameterized_csv/hello_world_test_named_parameterized.json")
    public void testNamedParameterized() throws Exception {
    }

}
