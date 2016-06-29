package org.jsmart.smarttester.core.tests;

import org.jsmart.smarttester.core.domain.SmartTestCase;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.runner.SmartJUnitRunner;
import org.jsmart.smarttester.core.tests.customrunner.TestOnlySmartJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@RunWith(TestOnlySmartJUnitRunner.class)
public class SmartJUnitRunnerTestCases {

    @SmartTestCase("07_some_test_cases/01_REST_end_point_GET_PASS.json")
    @Test
    public void testASmartTestCase() throws Exception {

    }

    @SmartTestCase("07_some_test_cases/02_java_service_single_step.json")
    @Test
    public void testASmartTestCase_Another() throws Exception {
    }

    @SmartTestCase("07_some_test_cases/non_existing_file.json")
    @Test
    public void testASmartTestCase_NonExistingFileJson() throws Exception {
    }

    @SmartTestCase("07_some_test_cases/03_REST_end_point_GET_REST_Execution_ERROR.json")
    @Test
    public void testASmartTestCase_DeliberateRESTErrorJson() throws Exception {
    }

    @SmartTestCase("07_some_test_cases/04_REST_with_request_response_path.json")
    @Test
    public void testASmartTestCase_request_response_path() throws Exception {

    }

    @SmartTestCase("07_some_test_cases/05_REST_with_request_response_path_1step.json")
    @Test
    public void testASmartTestCase_request_response__and_assertion_path() throws Exception {

    }
}
