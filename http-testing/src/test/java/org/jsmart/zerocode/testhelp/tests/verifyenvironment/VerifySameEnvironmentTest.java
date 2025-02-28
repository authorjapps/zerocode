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

import com.example.utils.PropertiesReader;


@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class VerifySameEnvironmentTest {

    @Test
    @Scenario("verify_environment/verify_same_environment.json")
    public void verifySameEnvironment() throws Exception {
        //Command to run this test: mvn test -Dtest="VerifySameEnvironmentTest#verifySameEnvironment" -Denv=Production
    }


    @AfterClass
    public static void afterTests() {

        try {
            String logFileLocation = PropertiesReader.getProperty("logfile.location");

            System.out.println("Log File Location In Property File: " + logFileLocation);
            File logFile = new File(logFileLocation);

            if (logFile.exists()) {
                String logs = new String(Files.readAllBytes(Paths.get(logFileLocation)));

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

