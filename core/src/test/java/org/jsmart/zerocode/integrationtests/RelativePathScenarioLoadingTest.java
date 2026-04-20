package org.jsmart.zerocode.integrationtests;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host = "http://localhost", port = 9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class RelativePathScenarioLoadingTest {

    @Test
    @Scenario("./src/test/resources/integration_test_files/helloworld/get_api_integration_test.json")
    public void testValidateSortedResponse() throws Exception {

    }

}



