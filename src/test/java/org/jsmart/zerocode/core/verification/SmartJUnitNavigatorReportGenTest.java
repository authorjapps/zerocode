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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.domain.reports.ZeroCodeReportProperties.TARGET_REPORT_DIR;

public class SmartJUnitNavigatorReportGenTest {

    @Test
    public void willGeneratReport_multiSceneMultiSteps() throws Exception {
        ObjectMapper mapper = new ObjectMapperProvider().get();
        Result result = JUnitCore.runClasses(SmartJUnitNavigatorReportGen.class);

        JSONAssert.assertEquals(
                SmartUtils.readJsonAsString("11_reports/03_multi_scenario_expected_report.json"),
                mapper.readValue(new File(TARGET_REPORT_DIR + "Will Get A bath Room Multi Multi.json"), JsonNode.class).toString(),
                false);

        assertThat(result.wasSuccessful(), is(true));

        System.out.println("### Final result: " + (result.wasSuccessful() ? "SUCCESS" : "FAILED"));

    }

}
