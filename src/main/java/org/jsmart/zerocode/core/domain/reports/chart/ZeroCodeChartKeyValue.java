package org.jsmart.zerocode.core.domain.reports.chart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeroCodeChartKeyValue {

    String key;
    Double value;
    private String result;

    public ZeroCodeChartKeyValue(
            @JsonProperty("key")String key,
            @JsonProperty("value")Double value,
            @JsonProperty("result")String result) {
        this.key = key;
        this.value = value;
        this.result = result;
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

    public String getResult() {
        return result;
    }
}
