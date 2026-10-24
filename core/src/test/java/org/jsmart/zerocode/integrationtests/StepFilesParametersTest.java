package org.jsmart.zerocode.integrationtests;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class StepFilesParametersTest {

    @Test
    @JsonTestCase("integration_test_files/multi_step/step_files_parameters_test.json")
    public void testSameStepFileWithDifferentParametersWillPassForEachReuse() throws Exception {

    }
}
