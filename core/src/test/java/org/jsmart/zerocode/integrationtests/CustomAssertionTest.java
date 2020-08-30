package org.jsmart.zerocode.integrationtests;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class CustomAssertionTest {
    
    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @Scenario("integration_test_files/custom_assert/execute_java_method_as_custom_assertions.json")
    public void testQueryParamsAsMap() throws Exception {
    
    }

}



