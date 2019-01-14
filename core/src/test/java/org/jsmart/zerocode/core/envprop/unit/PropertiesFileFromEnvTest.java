package org.jsmart.zerocode.core.envprop.unit;

import org.jsmart.zerocode.core.domain.EnvProperty;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.CustomRuntimeTestHttpClient;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@UseHttpClient(CustomRuntimeTestHttpClient.class) //<--- This sets some header also eg "key1"
@TargetEnv("config_hosts_test.properties")
@EnvProperty("_${env_property_key_name}")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class PropertiesFileFromEnvTest {

    @Test
    @JsonTestCase("01_verification_test_cases/04_custom_runtime_http_client.json")
    public void testASmartTestCase_createUpdate() throws Exception {

    }
}

