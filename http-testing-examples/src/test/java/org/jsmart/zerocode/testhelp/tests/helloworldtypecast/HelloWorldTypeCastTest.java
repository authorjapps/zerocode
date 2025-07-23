package org.jsmart.zerocode.testhelp.tests.helloworldtypecast;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldTypeCastTest {

    @Test
    @Scenario("parameterized_csv/hello_world_test_parameterized_csv_type_cast.json")
    public void testParameterized_typeCast() throws Exception {
    }

}
