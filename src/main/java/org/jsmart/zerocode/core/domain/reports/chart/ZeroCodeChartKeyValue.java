package org.jsmart.zerocode.core.domain.reports.chart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeroCodeChartKeyValue {

    String key;
    Double value;

    public ZeroCodeChartKeyValue(
            @JsonProperty("key")String key,
            @JsonProperty("value")Double value) {
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ZeroCodeChartKeyValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
