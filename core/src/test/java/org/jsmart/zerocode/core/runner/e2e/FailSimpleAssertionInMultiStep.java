package org.jsmart.zerocode.core.runner.e2e;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class FailSimpleAssertionInMultiStep {
	
	@Test
    @JsonTestCase("integration_test_files/failed_steps/01_two_step_one_fail.json")
    public void testFailSimpleAssertionInMultiStep_execAll() throws Exception {
    	
    }
	
}
