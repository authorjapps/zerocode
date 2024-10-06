package org.jsmart.zerocode.core.domain.reports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZeroCodeExecResult {
    private String scenarioName;
    private Integer loop;
    private List<ZeroCodeReportStep> steps = new ArrayList<>();
    private Map<String, List<String>> meta;
    
    @JsonCreator
    public ZeroCodeExecResult(
            @JsonProperty("scenarioName")String scenarioName,
            @JsonProperty("stepLoop")Integer loop,
            @JsonProperty("steps")List<ZeroCodeReportStep> steps,
            @JsonProperty("meta") Map<String, List<String>> meta) {
        this.scenarioName = scenarioName;
        this.loop = loop;
        this.steps = steps;
        this.meta = meta;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public Integer getLoop() {
        return loop;
    }

    public List<ZeroCodeReportStep> getSteps() {
        return steps;
    }

    public void setSteps(List<ZeroCodeReportStep> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "ZeroCodeExecResult{" +
                "scenarioName='" + scenarioName + '\'' +
                ", stepLoop=" + loop +
                ", steps=" + steps +
                '}';
    }

    public Map<String, List<String>> getMeta() {
        return meta;
    }

}
