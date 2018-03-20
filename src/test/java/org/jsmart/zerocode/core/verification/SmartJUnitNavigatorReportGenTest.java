package org.jsmart.zerocode.core.verification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;
import static org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl.getAllEndPointFilesFrom;

public class SmartJUnitNavigatorReportGenTest {
    ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void willGeneratReport_multiSceneMultiSteps() throws Exception {
        Result junitResult = JUnitCore.runClasses(SmartJUnitNavigatorReportGen.class);

        List<String> reportJsonFiles = getAllEndPointFilesFrom(TARGET_REPORT_DIR);

        final String reportFileUnderTest = reportJsonFiles.stream()
                .filter(thisReportFile -> thisReportFile.contains("Will Get A bath Room Multi Multi"))
                .findFirst()
                .get();

        JSONAssert.assertEquals(
                SmartUtils.readJsonAsString("11_reports/03_multi_scenario_expected_report.json"),
                //mapper.readValue(new File(TARGET_REPORT_DIR + "Will Get A bath Room Multi Multi.json"), JsonNode.class).toString(),
                mapper.readValue(new File(reportFileUnderTest), JsonNode.class).toString(),
                false);

        assertThat(junitResult.wasSuccessful(), is(true));

        System.out.println("### Final result: " + (junitResult.wasSuccessful() ? "PASSED" : "FAILED"));

    }

}
