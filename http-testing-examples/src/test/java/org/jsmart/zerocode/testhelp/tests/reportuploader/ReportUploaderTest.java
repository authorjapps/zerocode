package org.jsmart.zerocode.testhelp.tests.reportuploader;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@TargetEnv("report_uploader_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class ReportUploaderTest {

    @Test
    public void testGet() throws Exception {
        assertTrue(true);
    }
}
