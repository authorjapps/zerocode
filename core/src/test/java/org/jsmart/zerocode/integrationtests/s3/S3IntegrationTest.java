package org.jsmart.zerocode.integrationtests.s3;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("s3_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class S3IntegrationTest {

    @Ignore("Requires active AWS credentials and a valid S3 bucket to run")
    @Test
    @Scenario("integration_test_files/s3/s3_operations.json")
    public void testS3Operations() throws Exception {
    }
}
