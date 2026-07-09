package org.jsmart.zerocode.core.runner.retry;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Simulator-based integration tests for the retry DSL.
 *
 * Verifies that the "retry" field is correctly parsed from the JSON step
 * and does not break normal (passing) step execution.
 *
 * Simulator endpoints are defined in:
 *   test/resources/simulators/test_purpose_end_points.json
 */
@HostProperties(host = "http://localhost", port = 9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class RetryIntegrationTest {

    /**
     * Retry is configured (max=3) but the assertion passes on the first attempt.
     * Verifies that configuring retry does not break a step that succeeds immediately.
     */
    @Test
    @JsonTestCase("integration_test_files/retry_test_cases/01_retry_passes_on_first_attempt.json")
    public void retryPassesOnFirstAttempt() {
    }

}
