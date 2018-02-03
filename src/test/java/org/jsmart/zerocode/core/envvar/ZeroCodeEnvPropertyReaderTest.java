package org.jsmart.zerocode.core.envvar;

import org.jsmart.zerocode.core.domain.EnvProperty;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("app_config.properties")
@EnvProperty("${ENV_NAME}_") // see "ENV_NAME=ci" in .bash_profile file. If not found, then defaults to "app_config.properties"
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeEnvPropertyReaderTest {

    @JsonTestCase("01_verification_test_cases/09_host_port_annotated_bu_failed_assertion.json")
    @Test
    public void hostAnsPortWith_failedAssertions() throws Exception {
        
    }
}
