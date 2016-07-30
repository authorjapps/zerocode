package org.jsmart.zerocode.core.domain.reports.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeChartKeyValueArrayBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeChartKeyValueBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ZeroCodeChartKeyValueTest {
    private ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void serialize() throws Exception {

        ZeroCodeChartKeyValue kv1 = ZeroCodeChartKeyValueBuilder.newInstance().key("Scenario 1: Step1").value(100D).build();
        ZeroCodeChartKeyValue kv2 = ZeroCodeChartKeyValueBuilder.newInstance().key("Scenario 2: Step2").value(500D).build();

        List<ZeroCodeChartKeyValue> kvs = Arrays.asList(kv1, kv2);

        final String jsonString = mapper.writeValueAsString(kvs);

        assertThat(jsonString, containsString("{\"key\":\"Scenario 2: Step2\",\"value\":500.0}"));

    }

    @Test
    public void willGenertaeCommaSeparatedKV() throws Exception {
        /*
                var myArray = [
                ['Test Scenario1 -> Step1',1],
                ['Test Scenario2 -> Step2',10],
                ['Test Scenario3 -> Step3', 5]
                ];
        */

        ZeroCodeChartKeyValue kv1 = ZeroCodeChartKeyValueBuilder.newInstance().key("Scenario 1: Step1").value(100D).build();
        ZeroCodeChartKeyValue kv2 = ZeroCodeChartKeyValueBuilder.newInstance().key("Scenario 2: Step2").value(500D).build();

        List<ZeroCodeChartKeyValue> kvs = Arrays.asList(kv1, kv2);

        final String dataRowsCommaSeparated = kvs.stream()
                .map(thisRow -> String.format("['%s',%s]", thisRow.getKey(), thisRow.getValue()))
                .collect(Collectors.joining(", "));

        String dataArray = "[" + dataRowsCommaSeparated + "]";

        assertThat(dataArray, is("[['Scenario 1: Step1',100.0], ['Scenario 2: Step2',500.0]]"));

        String kvArray = ZeroCodeChartKeyValueArrayBuilder.newInstance().kvs(kvs).build();
        assertThat(kvArray, is("[['Scenario 1: Step1',100.0], ['Scenario 2: Step2',500.0]]"));

        String kvArrayAgain = ZeroCodeChartKeyValueArrayBuilder.newInstance()
                .kv(kv1)
                .kv(kv2)
                .build();
        assertThat(kvArrayAgain, is("[['Scenario 1: Step1',100.0], ['Scenario 2: Step2',500.0]]"));

    }
}