package org.jsmart.zerocode.core.runner.e2e;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class EchoStepShortForm {

    @Test
    @JsonTestCase("integration_test_files/echo_step/01_echo_step_short_form.json")
    public void testEchoStepShortForm() throws Exception {

    }

    @Test
    @JsonTestCase("integration_test_files/echo_step/02_java_call_without_assertions.json")
    public void testJavaCallWithoutAssertions() throws Exception {

    }
}
