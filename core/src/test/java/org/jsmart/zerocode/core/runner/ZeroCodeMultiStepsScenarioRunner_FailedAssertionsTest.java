package org.jsmart.zerocode.core.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TARGET_REPORT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.runner.e2e.FailSimpleAssertionInMultiStep;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ZeroCodeMultiStepsScenarioRunner_FailedAssertionsTest {
	
    public static final String SCENARIO_NAME = "Two Step - One Fail";

    private ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void test_FailSimpleAssertionInMultiStep() {
    	
        Result result = JUnitCore.runClasses(FailSimpleAssertionInMultiStep.class);
        assertThat(result.getRunCount(), is(1));

        File[] files = new File(TARGET_REPORT_DIR).listFiles((dir, fileName) -> fileName.endsWith(".json"));

        List<File> relevantReportFiles = Arrays.asList(files).stream()
                .filter(thisFile -> thisFile.getName().contains(SCENARIO_NAME) )
                .collect(Collectors.toList());

        assertThat(relevantReportFiles.size(), is(1));

        relevantReportFiles.forEach(this::testStep);
        
    }

    private void testStep(File thisFile) {
        ZeroCodeReport rawJsonReport = null;
        try {
            rawJsonReport = mapper.readValue(new File(thisFile.getAbsolutePath()), ZeroCodeReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (thisFile.getName().contains(SCENARIO_NAME)) {
            assertThat(rawJsonReport.getResults().size(), is(1));
            assertThat(rawJsonReport.getResults().get(0).getSteps().size(), is(2));

            assertThat(rawJsonReport.getResults().get(0).getSteps().get(0).getName(), is("FailedStep"));
            assertThat(rawJsonReport.getResults().get(0).getSteps().get(0).getResult(), is("FAILED"));
            assertThat(rawJsonReport.getResults().get(0).getSteps().get(0).getAssertions(), containsString("Assumed Payload"));
            assertThat(rawJsonReport.getResults().get(0).getSteps().get(0).getAssertions(), containsString("Assertion Errors"));
            assertThat(rawJsonReport.getResults().get(0).getSteps().get(0).getAssertions(), containsString("Assertion jsonPath"));

            assertThat(rawJsonReport.getResults().get(0).getSteps().get(1).getName(), is("PassedStep"));
            assertThat(rawJsonReport.getResults().get(0).getSteps().get(1).getResult(), is("PASSED"));
        }
    }

}



