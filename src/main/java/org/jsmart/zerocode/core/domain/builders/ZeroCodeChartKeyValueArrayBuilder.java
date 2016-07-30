package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.chart.ZeroCodeChartKeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZeroCodeChartKeyValueArrayBuilder {

    List<ZeroCodeChartKeyValue> kvs = new ArrayList<>();

    public static ZeroCodeChartKeyValueArrayBuilder newInstance() {
        return new ZeroCodeChartKeyValueArrayBuilder();
    }

    public String build() {
        final String dataRowsCommaSeparated = kvs.stream()
                .map(thisRow -> String.format("['%s',%s]", thisRow.getKey(), thisRow.getValue()))
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
