package org.jsmart.zerocode.core.kafka.consume;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerLocalConfigsWrap {
    private final ConsumerLocalConfigs consumerLocalConfigs;

    @JsonCreator
    public ConsumerLocalConfigsWrap(@JsonProperty("consumerLocalConfigs") ConsumerLocalConfigs consumerLocalConfigs) {
        this.consumerLocalConfigs = consumerLocalConfigs;
    }

    public ConsumerLocalConfigs getConsumerLocalConfigs() {
        return consumerLocalConfigs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumerLocalConfigsWrap that = (ConsumerLocalConfigsWrap) o;
        return Objects.equals(consumerLocalConfigs, that.consumerLocalConfigs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumerLocalConfigs);
    }

    @Override
    public String toString() {
        return "ConsumerLocalConfigsWrap{" +
                "consumerLocalConfigs=" + consumerLocalConfigs +
                '}';
    }
}
