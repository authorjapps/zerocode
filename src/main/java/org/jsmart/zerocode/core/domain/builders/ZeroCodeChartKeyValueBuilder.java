package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.chart.ZeroCodeChartKeyValue;

public class ZeroCodeChartKeyValueBuilder {

    String key;
    Double value;

    public static ZeroCodeChartKeyValueBuilder newInstance() {
        return new ZeroCodeChartKeyValueBuilder();
    }

    public ZeroCodeChartKeyValue build() {
        ZeroCodeChartKeyValue built = new ZeroCodeChartKeyValue(key, value);
        return built;
    }


    public ZeroCodeChartKeyValueBuilder key(String key) {
        this.key = key;
        return  this;
    }

    public ZeroCodeChartKeyValueBuilder value(Double value) {
        this.value = value;
        return  this;
    }
}
