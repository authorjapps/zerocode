package org.jsmart.zerocode.integrationtests;


import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class MultiStepTest {

    @Test
    @JsonTestCase("integration_test_files/multi_step/array_step_file_test.json")
    public void testMultiStep() throws Exception {

    }
}
