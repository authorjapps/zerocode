package org.jsmart.zerocode.core.verification;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;

@HostProperties(port=9998, context="/google-map-services")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeJUnitNavigatorHostContextAnnotation {

    @JsonTestCase("01_verification_test_cases/08_get_with_inline_host_port_context.json")
    @Test
    public void willPickFromClassAnnotation_hostAndPortNContext() throws Exception {

    }

}
