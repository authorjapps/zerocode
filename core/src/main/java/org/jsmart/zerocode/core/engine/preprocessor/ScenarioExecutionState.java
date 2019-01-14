/*
 * Copyright (C) 2014 jApps Ltd and
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
