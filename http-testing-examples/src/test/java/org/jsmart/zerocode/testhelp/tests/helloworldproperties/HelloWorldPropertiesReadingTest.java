package org.jsmart.zerocode.testhelp.tests.helloworldproperties;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("my_web_app.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldPropertiesReadingTest {

    @Test
    @Scenario("helloworld_properties_reading/read_properties_into_test_steps.json")
    public void test_aPropertyKeyValue() throws Exception {
    }

    @Test
    @Scenario("helloworld_properties_reading/use_common_SAML_token_as_headers.json")
    public void test_useCommonSAMLToken() throws Exception {
    }

}
