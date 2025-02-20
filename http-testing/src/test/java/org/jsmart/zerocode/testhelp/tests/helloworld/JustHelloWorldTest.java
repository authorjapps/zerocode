package org.jsmart.zerocode.testhelp.tests.helloworld;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.utils.SFTPLogUploader;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldTest {

    @Test
    @Scenario("helloworld/hello_world_status_ok_assertions.json")
    public void testGet() throws Exception {
    }

    @AfterClass
    public static void afterTests() {
        System.out.println("Uploading logs to SFTP...");
        SFTPLogUploader.uploadLogFile(); 
    }
}
