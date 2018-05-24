package org.jsmart.zerocode.core.verification.loopreport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;
import static org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl.getAllEndPointFilesFrom;

public class SmartJUnitNavigatorReportGenTest {
    ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void willGeneratReportLoopSceneMultiStepsLoop2_once() throws Exception {
        Result junitResult = JUnitCore.runClasses(SmartJUnitNavigatorReportGen.class);

        List<String> reportJsonFiles = getAllEndPointFilesFrom(TARGET_REPORT_DIR);

        final String reportFileUnderTest = reportJsonFiles.stream()
                .filter(thisReportFile -> thisReportFile.contains("Loop Scenario - Will Get A bath Room"))
                .findFirst()
                .get();

        JSONAssert.assertEquals(
                SmartUtils.readJsonAsString("11_reports/03_multi_scenario_expected_report.json"),
                //mapper.readValue(new File(TARGET_REPORT_DIR + "Will Get A bath Room Multi Multi.json"), JsonNode.class).toString(),
                mapper.readValue(new File(reportFileUnderTest), JsonNode.class).toString(),
                false);

        assertThat(junitResult.wasSuccessful(), is(true));

        String correlationId = retrieveCorrelationIdFromFileName(reportFileUnderTest);

        ZeroCodeReport stepReportContentPojo = mapper.readValue(new File(reportFileUnderTest), ZeroCodeReport.class);
        assertThat(stepReportContentPojo.getResults().toString(),
                containsString(correlationId));
    }

    @Test
    public void willGenerateloopReportIfRunLoop2AllPassRun_twice() throws Exception {
        Result junitResult = JUnitCore.runClasses(SmartJUnitNavigatorReportGen.class);
        Result junitResult2ndTime = JUnitCore.runClasses(SmartJUnitNavigatorReportGen.class);

        List<String> reportJsonFiles = getAllEndPointFilesFrom(TARGET_REPORT_DIR);

        List<String> reportFileNames = reportJsonFiles.stream()
                .filter(thisReportFile -> thisReportFile.contains("Loop Scenario - Will Get A bath Room"))
                .collect(Collectors.toList());

        assertThat(reportFileNames.size(), is(2));

    }

    @Test
    public void willGenerateloopReportIfRunLoop2OneFailRun_twice() throws Exception {
        Result junitResult = JUnitCore.runClasses(SmartJUnitNavigatorReportGenFailureStep.class);
        Result junitResult2ndTime = JUnitCore.runClasses(SmartJUnitNavigatorReportGenFailureStep.class);

        List<String> reportJsonFiles = getAllEndPointFilesFrom(TARGET_REPORT_DIR);

        List<String> reportFileNames = reportJsonFiles.stream()
                .filter(thisReportFile -> thisReportFile.contains("Loop Scenario - Will Get A bath Room with_a_failing_step"))
                .collect(Collectors.toList());

        assertThat(reportFileNames.size(), is(2));

        assertThat(junitResult.wasSuccessful(), is(false));
        assertThat(junitResult2ndTime.wasSuccessful(), is(false));

    }

    private String retrieveCorrelationIdFromFileName(String reportFileUnderTest) {
        String fileNameStartWith = "Loop Scenario - Will Get A bath Room";
        String fileNameWithXtn = (new File(reportFileUnderTest)).getName();
        String fileNameWithOutExt = FilenameUtils.removeExtension(fileNameWithXtn);

        return fileNameWithOutExt.substring(fileNameStartWith.length());
    }
}
