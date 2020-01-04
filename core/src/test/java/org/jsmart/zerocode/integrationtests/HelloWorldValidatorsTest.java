package org.jsmart.zerocode.integrationtests;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class HelloWorldValidatorsTest {

    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json.
     * @RunWith(TestOnlyZeroCodeUnitRunner.class) : starts these mocks first before running the tests
     */

    @Test
    @Scenario("integration_test_files/helloworld/get_api_integration_validators_test.json")
    public void testValidators() throws Exception {
    
    }

}



