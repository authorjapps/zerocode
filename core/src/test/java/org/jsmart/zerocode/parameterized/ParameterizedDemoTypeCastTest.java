package org.jsmart.zerocode.parameterized;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_world_host.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ParameterizedDemoTypeCastTest {

    @Test
    @JsonTestCase("integration_test_files/parameterized/parameterized_sample_type_cast_test.json")
    public void testParametrize_typeCast() throws Exception {
    }

}
