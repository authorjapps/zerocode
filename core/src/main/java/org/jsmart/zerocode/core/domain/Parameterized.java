package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Parameterized {
    private final List<Object> valueSource;
    private final List<String> csvSource;

    public Parameterized(
            @JsonProperty("valueSource") List<Object> valueSource,
            @JsonProperty("csvSource") List<String> csvSource) {
        this.valueSource = valueSource;
        this.csvSource = csvSource;
    }

    public List<Object> getValueSource() {
        return valueSource;
    }

    public List<String> getCsvSource() {
        return csvSource;
    }

    @Override
    public String toString() {
        return "Parameterized{" +
                "valueSource=" + valueSource +
                ", csvSource=" + csvSource +
                '}';
    }
}
