package org.jsmart.zerocode.core.envprop.unit;

import org.jsmart.zerocode.core.domain.EnvProperty;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("app_config.properties")
@EnvProperty("_${ENV_NAME}") // see "ENV_NAME=ci" in .bash_profile file. If not found, then defaults to "app_config.properties"
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeEnvPropertyReaderTest {

    @Test
    @JsonTestCase("14_env_prop/22_env_property_dynamic_runtime.json")
    public void testRunAgainstConfigPropertySetViaJenkins() throws Exception {
        
    }
}
