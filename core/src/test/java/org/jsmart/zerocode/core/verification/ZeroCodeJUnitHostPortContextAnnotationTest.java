package org.jsmart.zerocode.core.verification;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(port=9998, context="/google-map-services")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeJUnitHostPortContextAnnotationTest {

    @JsonTestCase("01_verification_test_cases/08_get_with_inline_host_port_context.json")
    @Test
    public void willPickFromClassAnnotation_hostAndPortNContext() throws Exception {

    }

}
