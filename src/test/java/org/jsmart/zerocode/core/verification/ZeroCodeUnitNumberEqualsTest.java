package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.CustomRuntimeTestHttpClient;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeUnitNumberEqualsTest {

    @Test
    @JsonTestCase("01_verification_test_cases/05_number_equals_test.json")
    public void willPassIfEqualsToSame_number() throws Exception {

    }
}

