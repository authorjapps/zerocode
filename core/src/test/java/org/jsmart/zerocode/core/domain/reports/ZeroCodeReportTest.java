package org.jsmart.zerocode.core.domain.reports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeIoWriteBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportStepBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.TARGET_REPORT_DIR;

public class ZeroCodeReportTest {
    public static final String SCENARIO_1 = "Unique Scenario 1";
    public static final String SCENARIO_2 = "Unique Scenario 2";

    private ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void willSerialize_ToJson() throws Exception {

        ZeroCodeReport javaBuilt = ZeroCodeIoWriteBuilder.newInstance()
                .timeStamp(LocalDateTime.now())
                .results(Arrays.asList(ZeroCodeExecReportBuilder.newInstance()
                        .scenarioName("scenario name")
                        .loop(1)
                        .steps(Arrays.asList(ZeroCodeReportStepBuilder.newInstance()
                                .loop(3)
                                .correlationId("correlation Id")
                                .name("step_create")
                                .method("POST-POST")
                                .url("/home/googly")
                                .result("PASS")
                                .build()))
                        .build()))
                .build();

        JsonNode jsonNode = mapper.valueToTree(javaBuilt);

        assertThat(jsonNode.get("timeStamp"), is(notNullValue()));
        assertThat(jsonNode.get("results").get(0).get("loop").asInt(), is(1));
        assertThat(jsonNode.get("results").get(0).get("steps").get(0).get("loop").asInt(), is(3));
        assertThat(jsonNode.get("results").get(0).get("steps").get(0).get("method").asText(), is("POST-POST"));
    }

    @Test
    public void willDeSerialize_FromJson() throws Exception {

        ZeroCodeReport jsonDeDone = mapper.readValue(
                SmartUtils.readJsonAsString("unit_test_files/reports/01_basic_report_for_test.json"),
                ZeroCodeReport.class
        );

        assertThat(jsonDeDone.getTimeStamp(), is(notNullValue()));
        assertThat(jsonDeDone.getResults().get(0).getLoop(), is(10));
        assertThat(jsonDeDone.getResults().get(0).getSteps().get(0).getLoop(), is(30));
        assertThat(jsonDeDone.getResults().get(0).getSteps().get(0).getResult(), is("PASS-PASS"));
    }

    @Test
    public void testReport_correct_scenariosAndSteps() {
        Result result = JUnitCore.runClasses(ZeroCodeRunTwoScenariosForReport.class);
        assertThat(result.getRunCount(), is(2));

        File[] files = new File(TARGET_REPORT_DIR).listFiles((dir, fileName) -> fileName.endsWith(".json"));

        List<File> relevantReportFiles = Arrays.asList(files).stream()
                .filter(thisFile -> thisFile.getName().contains(SCENARIO_1) || thisFile.getName().contains(SCENARIO_2))
                .collect(Collectors.toList());

        assertThat(relevantReportFiles.size(), is(2));

        relevantReportFiles.forEach(this::testStepSize);
    }

    private void testStepSize(File thisFile) {
        ZeroCodeReport rawJsonReport = null;
        try {
            rawJsonReport = mapper.readValue(new File(thisFile.getAbsolutePath()), ZeroCodeReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (thisFile.getName().contains(SCENARIO_1)) {
            assertThat(rawJsonReport.getResults().size(), is(1));
            assertThat(rawJsonReport.getResults().get(0).getSteps().size(), is(2));

        } else if (thisFile.getName().contains(SCENARIO_2)) {
            assertThat(rawJsonReport.getResults().size(), is(1));
            assertThat(rawJsonReport.getResults().get(0).getSteps().size(), is(2));
        }
    }

}