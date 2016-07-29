package org.jsmart.zerocode.core.tests;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class SmartJUnitRunnerTestCases {

    @JsonTestCase("07_some_test_cases/01_REST_end_point_GET_PASS.json")
    @Test
    public void testASmartTestCase() throws Exception {

    }

    @JsonTestCase("07_some_test_cases/02_java_service_single_step.json")
    @Test
    public void testASmartTestCase_Another() throws Exception {
    }

    @JsonTestCase("07_some_test_cases/non_existing_file.json")
    @Test
    public void testASmartTestCase_NonExistingFileJson() throws Exception {
    }

    @JsonTestCase("07_some_test_cases/03_REST_end_point_GET_REST_Execution_ERROR.json")
    @Test
    public void testASmartTestCase_DeliberateRESTErrorJson() throws Exception {
    }

    @JsonTestCase("07_some_test_cases/04_REST_with_request_response_path.json")
    @Test
    public void testASmartTestCase_request_response_path() throws Exception {

    }

    @JsonTestCase("07_some_test_cases/05_REST_with_request_response_path_1step.json")
    @Test
    public void testASmartTestCase_request_response__and_assertion_path() throws Exception {

    }

    @Test
    @JsonTestCase("07_some_test_cases/06_will_mock_using_wiremock_and_run_other_steps.json")
    public void willMockAndRunNextStep() throws Exception {

    }

    @Test
    @JsonTestCase("07_some_test_cases/07_REST_with_loop_test.json")
    public void restViaLoop() throws Exception {

    }


}
