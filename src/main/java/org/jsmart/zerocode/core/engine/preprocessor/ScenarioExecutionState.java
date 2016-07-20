package org.jsmart.zerocode.core.engine.preprocessor;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScenarioExecutionState {
    private String scenarioStateTemplate = "{\n" +
            "  ${STEP_REQUEST_RESPONSE_SECTION}\n" +
            "}";

    List<StepExecutionState> allSteps = new ArrayList<>();
    List<String> allStepsInStringList = new ArrayList<>();

    Map<String, String> paramMap = new HashMap<>();

    public ScenarioExecutionState() {
        //SmartUtils.readJsonAsString("engine/request_respone_template_step.json");
    }

    public String getScenarioStateTemplate() {
        return scenarioStateTemplate;
    }

    public void setScenarioStateTemplate(String scenarioStateTemplate) {
        this.scenarioStateTemplate = scenarioStateTemplate;
    }

    public List<StepExecutionState> getAllSteps() {
        return allSteps;
    }

    public void setAllSteps(List<StepExecutionState> allSteps) {
        this.allSteps = allSteps;
    }

    public List<String> getAllStepsInStringList() {
        return allStepsInStringList;
    }

    public void setAllStepsInStringList(List<String> allStepsInStringList) {
        this.allStepsInStringList = allStepsInStringList;
    }

    public void addStepState(String stepState){
        allStepsInStringList.add(stepState);
    }

    public String getResolvedScenarioState() {
        final String commaSeparatedStepResults = getAllStepsInStringList().stream()
                .map(i -> i)
                .collect(Collectors.joining(", "));
        paramMap.put("STEP_REQUEST_RESPONSE_SECTION", commaSeparatedStepResults);

        return (new StrSubstitutor(paramMap)).replace(scenarioStateTemplate);
    }
}
