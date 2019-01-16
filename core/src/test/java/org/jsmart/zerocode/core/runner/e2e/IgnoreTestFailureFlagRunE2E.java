package org.jsmart.zerocode.core.runner.e2e;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class IgnoreTestFailureFlagRunE2E {
    
    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @JsonTestCase("01_verification_test_cases/22_ignoreStepFailures_exec_all.json")
    public void testMultiStepIgnoreStepFailures_execAll() throws Exception {
    }

}



