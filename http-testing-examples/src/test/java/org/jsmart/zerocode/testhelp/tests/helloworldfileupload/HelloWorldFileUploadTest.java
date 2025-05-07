package org.jsmart.zerocode.testhelp.tests.helloworldfileupload;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("postman_echo_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldFileUploadTest {

    @Test
    @Scenario("helloworld_file_upload/hello_world_file_upload_test.json")
    public void testFileUpload() throws Exception {
    }
}
