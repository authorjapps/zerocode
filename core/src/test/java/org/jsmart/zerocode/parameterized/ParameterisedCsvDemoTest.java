package org.jsmart.zerocode.parameterized;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("app_config.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class ParameterisedCsvDemoTest {

    @Test
    @JsonTestCase("integration_test_files/parameterized/parameterized_sample_csv_test.json")
    public void testParameterizedCsv() throws Exception {
    }

    @Test
    @JsonTestCase("integration_test_files/parameterized/parameterized_sample_csv_with_headers_test.json")
    public void testParameterizedCsvWithHeaders() throws Exception {
    }
}
