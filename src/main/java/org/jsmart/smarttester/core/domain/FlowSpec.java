package org.jsmart.smarttester.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FlowSpec {

    private final Integer loop;
    private final String flowName;
    private final List<Step> steps;

    @JsonCreator
    public FlowSpec(
            @JsonProperty("loop") Integer loop,
            @JsonProperty("flowName") String flowName,
            @JsonProperty("steps") List<Step> steps) {
        this.loop = loop;
        this.flowName = flowName;
        this.steps = steps;
    }

    public Integer getLoop() {
        return loop;
    }

    public String getFlowName() {
        return flowName;
    }

    public List<Step> getSteps() {
        return steps == null? (new ArrayList<>()) : steps;
    }

    @Override
    public String toString() {
        return "FlowSpec{" +
                "loop=" + loop +
                ", flowName='" + flowName + '\'' +
                ", steps=" + steps +
                '}';
    }
}
