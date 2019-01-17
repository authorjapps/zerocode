package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.chart.ZeroCodeChartKeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZeroCodeChartKeyValueArrayBuilder {

    public static final String TEST_FAILED = "FAILED";

    List<ZeroCodeChartKeyValue> kvs = new ArrayList<>();

    public static ZeroCodeChartKeyValueArrayBuilder newInstance() {
        return new ZeroCodeChartKeyValueArrayBuilder();
    }

    /*
        var processed_test_result = [{
            name: 'Will Mock an End Point and Test the end point using Zerocode->setup_mocks',
            y: 429.0
        }, {
            name: 'Will Mock an End Point and Test the end point using Zerocode->actual_test_verify_get_customer',
            color: '#FF0000',
            y: 92.0
        }];
    */
    public String build() {
        final String dataRowsCommaSeparated = kvs.stream()
                .map(thisRow -> {
                            if (TEST_FAILED.equals(thisRow.getResult())) {
                                return String.format("{name: '%s', y: %s, color: '#FF0000'}", thisRow.getKey(), thisRow.getValue());
                            } else {
                                return String.format("{name: '%s', y: %s}", thisRow.getKey(), thisRow.getValue());
                            }

                        }

                )
                .collect(Collectors.joining(", "));

        String dataArray = "[" + dataRowsCommaSeparated + "]";

        return dataArray;
    }

    public ZeroCodeChartKeyValueArrayBuilder kvs(List<ZeroCodeChartKeyValue> kvs) {
        this.kvs = kvs;
        return this;
    }

    public ZeroCodeChartKeyValueArrayBuilder kv(ZeroCodeChartKeyValue kv) {
        this.kvs.add(kv);
        return this;
    }

}
