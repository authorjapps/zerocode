package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(port=9998, context="/google-map-services")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeFailedAssertionsTestRunner {

    @JsonTestCase("01_verification_test_cases/09_host_port_annotated_bu_failed_assertion.json")
    @Test
    public void hostAnsPortWith_failedAssertions() throws Exception {
        
    }
}
