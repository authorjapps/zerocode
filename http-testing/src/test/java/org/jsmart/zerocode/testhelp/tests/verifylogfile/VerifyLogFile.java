package org.jsmart.zerocode.testhelp.tests.verifylogfile;

import java.io.File;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.utils.PropertiesReader;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class VerifyLogFile {

    @Test
    @Scenario("logfile/verify_log_file_created.json")
    public void testLogFile() throws Exception {
        
    }

    @AfterClass
    public static void afterTests() {

        String logFileLocation = PropertiesReader.getProperty("logfile.location");

        System.out.println("Log File Location In Property File: " + logFileLocation);

        File logFile = new File(logFileLocation);
        if (!logFile.exists()) {
            //Unable to find the log file.
            System.out.println("Log file does not exist: " + logFile);
            return;
        }
        else
        {
            System.out.println("Found log file: " + logFile);
        }
    }

}
