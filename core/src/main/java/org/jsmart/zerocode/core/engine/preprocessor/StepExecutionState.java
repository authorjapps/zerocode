package org.jsmart.zerocode.core.engine.preprocessor;

import org.apache.commons.text.StringSubstitutor;
import org.jsmart.zerocode.core.domain.Step;

import java.util.HashMap;
import java.util.Map;

public class StepExecutionState {
    Map<String, String> paramMap = new HashMap<>();
    private Step step;
    private String stepName;

    private String requestResponseState = "\"${STEP.NAME}\": {\n" +
            "    \"request\":${STEP.REQUEST},\n" +
            "    \"response\": ${STEP.RESPONSE}\n" +
            "  }";

    public StepExecutionState() {
        //SmartUtils.readJsonAsString("engine/request_respone_template_scene.json");
    }

    public void addStep(Step step) {
        this.step = step;
        this.stepName = step.getName();
        paramMap.put("STEP.NAME", stepName);
    }

    public void addRequest(String requestJson) {
        paramMap.put("STEP.REQUEST", requestJson);

    }

    public void addResponse(String responseJson) {
        paramMap.put("STEP.RESPONSE", responseJson);
    }

    public String getResolvedStep() {
        StringSubstitutor sub = new StringSubstitutor(paramMap);
        return sub.replace(requestResponseState);
    }

    public String getStepName() {
        return stepName;
    }

    public Step getStep() {
        return step;
    }
}
