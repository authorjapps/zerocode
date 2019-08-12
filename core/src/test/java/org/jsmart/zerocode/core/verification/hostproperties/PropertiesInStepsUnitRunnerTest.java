package org.jsmart.zerocode.core.verification.hostproperties;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class PropertiesInStepsUnitRunnerTest {

    @Test
    @JsonTestCase("integration_test_files/host_keys/property_reading_into_test_step_test.json")
    public void testReading_propertyValuesViaKey() throws Exception {

    }
}

