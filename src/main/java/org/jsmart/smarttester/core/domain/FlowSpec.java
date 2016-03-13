package org.jsmart.smarttester.core.domain;

import java.util.List;

public class FlowSpec {

    private final String flow;
    private List<Step> steps; // = new ArrayList<Step>();

    public FlowSpec(String flow, List<Step> steps) {
        this.flow = flow;
        this.steps = steps;
    }

    public String getFlow() {
        return flow;
    }

    public List<Step> getSteps() {
        return steps;
        //Optional.ofNullable(steps)
        //.orElse(Collections.<Step>emptyList());
    }
}
