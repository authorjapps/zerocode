package org.jsmart.zerocode.integrationtests;


import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class CustomLogTest {

    @Test
    @JsonTestCase("integration_test_files/custom_log/step_files.json")
    public void testStrict_compareStrict() throws Exception {

    }
}
