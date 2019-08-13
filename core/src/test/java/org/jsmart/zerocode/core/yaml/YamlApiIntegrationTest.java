package org.jsmart.zerocode.core.yaml;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class YamlApiIntegrationTest {

    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json.
     * @RunWith(TestOnlyZeroCodeUnitRunner.class) : starts these mocks first before running the tests
     */

    @Test
    @JsonTestCase("integration_test_files/get_api/simple_get_api_test.json")
    public void testSimpleGetApi_jsonSanity() throws Exception {
    
    }

    @Test
    @Scenario("integration_test_files/yaml/simple_get_api_test.yml")
    public void testSimpleGetApi_yaml() throws Exception {

    }

    @Test
    @Scenario("integration_test_files/yaml/string_optional_double_quotes_test.yml")
    public void testSimpleGetApiOptional_doubleQuatedStringYaml() throws Exception {

    }

    @Test
    @Scenario("integration_test_files/yaml/simple_get_api_multi_step_test.yml")
    public void testSimpleGetApi_multiStepYaml() throws Exception {

    }

}



