package org.jsmart.zerocode.integrationtests.masked;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class MaskedSecretsInMemoryTest {

    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json.
     * @RunWith(TestOnlyZeroCodeUnitRunner.class) : starts these mocks first before running the tests
     */

    @Test
    @Scenario("integration_test_files/masked/password_or_secrets_masked_in_log_n_console.json")
    public void testSecretPrinted_masked() throws Exception {
    }

    @Test
    @Scenario("integration_test_files/masked/bearer_token_or_secret_masked_reuse_example_.json")
    public void testSecretPrintedAssertions_masked() throws Exception {

    }

}



