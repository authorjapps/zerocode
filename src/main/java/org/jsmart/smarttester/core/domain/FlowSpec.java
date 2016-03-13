package org.jsmart.smarttester.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FlowSpec {

    private final Integer loop;
    private final String flowName;
    private List<Step> steps; // = new ArrayList<Step>();

    @JsonCreator
    public FlowSpec(
            @JsonProperty("loop") Integer loop,
            @JsonProperty("flowName") String flowName,
            @JsonProperty("steps") List<Step> steps) {
        this.loop = loop;
        this.flowName = flowName;
        this.steps = steps;
    }

    public String getFlowName() {
        return flowName;
    }

    public List<Step> getSteps() {
        // return steps;
        return Optional.ofNullable(steps)
                .orElse(Collections.<Step>emptyList());
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
