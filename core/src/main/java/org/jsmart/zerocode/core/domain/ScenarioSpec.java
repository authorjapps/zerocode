package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScenarioSpec {

    private final Integer loop;
    private final Boolean ignoreStepFailures;
    private final String scenarioName;
    private final List<Step> steps;
    private final Parameterized parameterized;

    @JsonCreator
    public ScenarioSpec(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("ignoreStepFailures") Boolean ignoreStepFailures,
            @JsonProperty("scenarioName") String scenarioName,
            @JsonProperty("steps") List<Step> steps,
            @JsonProperty("parameterized") Parameterized parameterized) {
        this.loop = loop;
        this.ignoreStepFailures = ignoreStepFailures;
        this.scenarioName = scenarioName;
        this.steps = steps;
        this.parameterized = parameterized;
    }

    public Integer getLoop() {
        return loop;
    }

    public Boolean getIgnoreStepFailures() {
        return ignoreStepFailures;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public List<Step> getSteps() {
        return steps == null? (new ArrayList<>()) : steps;
    }

    public Parameterized getParameterized() {
        return parameterized;
    }

    @Override
    public String toString() {
        return "ScenarioSpec{" +
                "loop=" + loop +
                ", ignoreStepFailures=" + ignoreStepFailures +
                ", scenarioName='" + scenarioName + '\'' +
                ", steps=" + steps +
                ", parameterized=" + parameterized +
                '}';
    }
}
