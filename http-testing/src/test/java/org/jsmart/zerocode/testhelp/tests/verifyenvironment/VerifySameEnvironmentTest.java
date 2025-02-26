package org.jsmart.zerocode.testhelp.tests.verifyenvironment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;


@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class VerifySameEnvironmentTest {

    //TODO: Get Log Relative Path
    private static final String LOG_FILE_PATH = "C:\\WSU\\CPTS_581_Project\\SkipEnvironment_3\\zerocode\\http-testing\\target\\logs\\your_app_tests_logs.log"; // ZeroCode log file


    @Test
    @Scenario("verify_environment/verify_same_environment.json")
    public void verifySameEnvironment() throws Exception {
        //Command to run this test: mvn test -Dtest="VerifySameEnvironmentTest#verifySameEnvironment" -Denv=Production
    }


    @AfterClass
    public static void afterTests() {

        try {
            File logFile = new File(LOG_FILE_PATH);

            if (logFile.exists()) {
                String logs = new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)));

                // Print logs for debugging
                //System.out.println("Captured Logs from Report:\n" + logs);

                // Check if the step was skipped
                if (logs.contains("Skipping step 'verify_same_environment'"))
                {
                    System.out.println("Test was skipped successfully due to environment being excluded");
                }
             } 
             else 
             {
                System.out.println("ZeroCode log file not found! Test results could not be verified.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

