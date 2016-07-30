package org.jsmart.zerocode.core.domain.reports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportStepBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZeroCodeReportTest {
    private ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void willSerialize_ToJson() throws Exception {

        ZeroCodeReport javaBuilt = ZeroCodeReportBuilder.newInstance()
                .timeStamp(LocalDateTime.now())
                .results(Arrays.asList(ZeroCodeExecResultBuilder.newInstance()
                        .scenarioName("scenario name")
                        .loop(1)
                        .steps(Arrays.asList(ZeroCodeReportStepBuilder.newInstance()
                                .loop(3)
                                .correlationId("correlation Id")
                                .name("step_create")
                                .operation("POST-POST")
                                .url("/home/googly")
                                .result("PASS")
                                .build()))
                        .build()))
                .build();

        JsonNode jsonNode = mapper.valueToTree(javaBuilt);

        assertThat(jsonNode.get("timeStamp"), is(notNullValue()));
        assertThat(jsonNode.get("results").get(0).get("loop").asInt(), is(1));
        assertThat(jsonNode.get("results").get(0).get("steps").get(0).get("loop").asInt(), is(3));
    }

    @Test
    public void willDeSerialize_FromJson() throws Exception {

        ZeroCodeReport jsonDeDone = mapper.readValue(
                SmartUtils.readJsonAsString("11_reports/01_basic_report_for_test.json"),
                ZeroCodeReport.class
        );

        assertThat(jsonDeDone.getTimeStamp(), is(notNullValue()));
        assertThat(jsonDeDone.getResults().get(0).getLoop(), is(10));
        assertThat(jsonDeDone.getResults().get(0).getSteps().get(0).getLoop(), is(30));
        assertThat(jsonDeDone.getResults().get(0).getSteps().get(0).getResult(), is("PASS-PASS"));

    }
}