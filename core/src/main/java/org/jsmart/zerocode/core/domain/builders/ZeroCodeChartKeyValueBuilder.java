package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.chart.ZeroCodeChartKeyValue;

public class ZeroCodeChartKeyValueBuilder {

    String key;
    Double value;
    String result;

    public static ZeroCodeChartKeyValueBuilder newInstance() {
        return new ZeroCodeChartKeyValueBuilder();
    }

    public ZeroCodeChartKeyValue build() {
        ZeroCodeChartKeyValue built = new ZeroCodeChartKeyValue(key, value, result);
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

    public ZeroCodeChartKeyValueBuilder result(String result) {
        this.result = result;
        return  this;
    }
}
