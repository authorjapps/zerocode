package org.jsmart.zerocode.filebody;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_world_host.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ReuseFileBodyTest {

    @Test
    @JsonTestCase("integration_test_files/filebody/hello_world_all_integrated_apis.json")
    public void testHelloWorld_AllApi() throws Exception {
    }

    @Test
    @JsonTestCase("integration_test_files/filebody/hello_world_file_body.json")
    public void test_fileBody() throws Exception {
    }

    @Test
    @JsonTestCase("integration_test_files/filebody/hello_world_file_request_n_response.json")
    public void test_fileBodyAndAssertions() throws Exception {
    }

}
