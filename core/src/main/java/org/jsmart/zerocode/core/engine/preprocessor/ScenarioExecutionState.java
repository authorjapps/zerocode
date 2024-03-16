package org.jsmart.zerocode.core.engine.preprocessor;

import org.apache.commons.text.StringSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScenarioExecutionState {
    private String scenarioStateTemplate = "{\n" +
            "  ${STEP_REQUEST_RESPONSE_SECTION}\n" +
            "}";


    Map<String, StepExecutionState> allStepsLinkedMap = new LinkedHashMap<>();

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

    public Optional<StepExecutionState> getExecutedStepState(String stepName) {
        return Optional.of(allStepsLinkedMap.get(stepName));
    }

    public List<StepExecutionState> getAllSteps() {
        return new ArrayList<>(allStepsLinkedMap.values());
    }

    public List<String> getAllStepsInStringList() {
        return allStepsLinkedMap.values()
                .stream().map(StepExecutionState::getResolvedStep)
                .collect(Collectors.toList());
    }

    public void addStepState(StepExecutionState stepState){
        //removing key so that order of step state is changed
        allStepsLinkedMap.remove(stepState.getStepName());
        allStepsLinkedMap.put(stepState.getStepName(), stepState);
    }

    public String getResolvedScenarioState() {
        final String commaSeparatedStepResults = String.join(", ", getAllStepsInStringList());
        paramMap.put("STEP_REQUEST_RESPONSE_SECTION", commaSeparatedStepResults);

        return (new StringSubstitutor(paramMap)).replace(scenarioStateTemplate);
    }
}
