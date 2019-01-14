package org.jsmart.zerocode.core.verification;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;

//@TargetEnv("config_hosts.properties")
//@RunWith(ZeroCodeUnitRunner.class)
@HostProperties(host="http://localhost", port=9998, context="")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeJUnitNavigatorHostAnnotation {

    @JsonTestCase("01_verification_test_cases/07_get_more_bathroom_inline_host_port.json")
    @Test
    public void willPickFromClassAnnotation_hostAndPort() throws Exception {

    }

}
