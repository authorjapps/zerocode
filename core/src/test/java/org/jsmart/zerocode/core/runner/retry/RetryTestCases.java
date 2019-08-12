package org.jsmart.zerocode.core.runner.retry;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("dev_test.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class RetryTestCases {

    @Test
    @JsonTestCase("integration_test_files/retry_test_cases/01_REST_with_retry_test.json")
    public void restRetry() {

    }

    @Test
    @JsonTestCase("integration_test_files/retry_test_cases/02_REST_with_retry_within_loop_test.json")
    public void restRetryWithinLoop() {

    }

    @Test
    @JsonTestCase("integration_test_files/retry_test_cases/03_failing_REST_with_retry_within_loop_test.json")
    public void failingRestRetryWithinLoop() {

    }
}
