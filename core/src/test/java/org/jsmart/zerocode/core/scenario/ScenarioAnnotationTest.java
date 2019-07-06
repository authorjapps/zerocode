package org.jsmart.zerocode.core.scenario;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ScenarioAnnotationTest {

    @Test
    @Scenario("01_verification_test_cases/05_number_equals_test.json")
    public void willPassIfEqualsToSame_number() throws Exception {

    }
}

