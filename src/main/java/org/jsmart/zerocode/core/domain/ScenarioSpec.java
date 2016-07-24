package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ScenarioSpec {

    private final Integer loop;
    private final String scenarioName;
    private final List<Step> steps;

    @JsonCreator
    public ScenarioSpec(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("scenarioName") String scenarioName,
            @JsonProperty("steps") List<Step> steps) {
        this.loop = loop;
        this.scenarioName = scenarioName;
        this.steps = steps;
    }

    public Integer getLoop() {
        return loop;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public List<Step> getSteps() {
        return steps == null? (new ArrayList<>()) : steps;
    }

    @Override
    public String toString() {
        return "ScenarioSpec{" +
                "stepLoop=" + loop +
                ", scenarioName='" + scenarioName + '\'' +
                ", steps=" + steps +
                '}';
    }
}
