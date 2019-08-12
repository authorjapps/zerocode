package org.jsmart.zerocode.core.runner.retry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.constants.ZeroCodeReportConstants;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TARGET_REPORT_DIR;

public class ZeroCodeMultiStepsScenarioRunnerImplRetryTest {

    private static final ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void testRetryScenarios() {

        final String SCENARIO_RETRY = "Rest with Retry Test";
        final String SCENARIO_RETRY_LOOP = "Rest with Retry within loop Test";
        final String SCENARIO_FAILED_RETRY_LOOP = "Failing Rest with Retry within loop Test";

        // mvn test without clean, or launching the test repeatedly from within the ide,
        // leaves the reports from the previous run in the target-directory.
        // Because we use the reports of *this* run to assert, we clean out the old ones.
        deleteScenarioReport(SCENARIO_RETRY);
        deleteScenarioReport(SCENARIO_RETRY_LOOP);
        deleteScenarioReport(SCENARIO_FAILED_RETRY_LOOP);

        Result result = JUnitCore.runClasses(RetryTestCases.class);
        assertThat(result.getRunCount(), is(3));

        ZeroCodeReport restWithRetryReport = getScenarioReport(SCENARIO_RETRY);
        // loop: 0, retry-max: 12
        // the first attempts fails, the second one succeeds, which ends the retry mechanism
        // note that the first step in all scenarios is the call to wiremock, hence the index starts at 1
        assertStepFailed(restWithRetryReport, 1);
        assertStepSucceeded(restWithRetryReport, 2);

        ZeroCodeReport restWithRetryWithinLoopReport = getScenarioReport(SCENARIO_RETRY_LOOP);
        // loop: 0, retry-max: 3
        // in the first loop-iteration, the first attempt fails. The second one succeeds, ending this loop-iteration
        assertStepFailed(restWithRetryReport, 1);
        assertStepSucceeded(restWithRetryWithinLoopReport, 2);
        assertStepCount(restWithRetryWithinLoopReport, 3);

        ZeroCodeReport failingRestWithRetryWithinLoopReport = getScenarioReport(SCENARIO_FAILED_RETRY_LOOP);
        // loop: 0, retry-max: 3
        // all requests fail: it retries 3 times in the first loop-iteration
        // This makes the first loop-iteration fail, so the second is not executed
        // so we expect to see only 3 failed attempts
        assertStepFailed(failingRestWithRetryWithinLoopReport, 1);
        assertStepFailed(failingRestWithRetryWithinLoopReport, 2);
        assertStepFailed(failingRestWithRetryWithinLoopReport, 3);

        assertStepCount(failingRestWithRetryWithinLoopReport, 4);

    }

    private void deleteScenarioReport(String scenarioName) {
        List<File> scenarioReportFiles = findScenarioReportFiles(scenarioName);
        for (File file : scenarioReportFiles) {
            file.delete();
        }
    }

    private ZeroCodeReport getScenarioReport(String scenarioName) {
        List<File> scenarioReportFiles = findScenarioReportFiles(scenarioName);
        assertThat(scenarioReportFiles.size(), is(1));
        return fileToZeroCodeReport(scenarioReportFiles.get(0));
    }

    private void assertStepSucceeded(ZeroCodeReport report, int stepIndex) {
        ZeroCodeReportStep step = getStep(report, stepIndex);
        assertThat(step.getResult(), is(ZeroCodeReportConstants.RESULT_PASS));
    }

    private void assertStepFailed(ZeroCodeReport report, int stepIndex) {
        ZeroCodeReportStep step = getStep(report, stepIndex);
        assertThat(step.getResult(), is(ZeroCodeReportConstants.RESULT_FAIL));
    }

    private void assertStepCount(ZeroCodeReport report, int stepCount) {
        assertThat(report.getResults().get(0).getSteps().size(), is(stepCount));
    }

    private ZeroCodeReportStep getStep(ZeroCodeReport report, int stepIndex) {
        return report.getResults().get(0).getSteps().get(stepIndex);
    }


    private List<File> findScenarioReportFiles(String scenarioName) {
        File[] files = new File(TARGET_REPORT_DIR).listFiles((dir, fileName) -> fileName.matches(scenarioName + "([a-f0-9-]*)\\.json$"));
        if ( files == null ) {
            return new ArrayList<>();
        }
        return Arrays.asList(files);
    }

    private ZeroCodeReport fileToZeroCodeReport(File thisFile) {
        try {
            return mapper.readValue(new File(thisFile.getAbsolutePath()), ZeroCodeReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
