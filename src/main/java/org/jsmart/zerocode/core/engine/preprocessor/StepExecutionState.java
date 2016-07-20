package org.jsmart.zerocode.core.engine.preprocessor;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class StepExecutionState {
    Map<String, String> paramMap = new HashMap<>();

    private static String requestResponseState = "\"${STEP.NAME}\": {\n" +
            "    \"request\":${STEP.REQUEST},\n" +
            "    \"response\": ${STEP.RESPONSE}\n" +
            "  }";

    public StepExecutionState() {
        //SmartUtils.readJsonAsString("engine/request_respone_template_scene.json");
    }

    public static String getRequestResponseState() {
        return requestResponseState;
    }

    public void setRequestResponseState(String requestResponseState) {
        this.requestResponseState = requestResponseState;
    }

    public void addStep(String stepName) {
        paramMap.put("STEP.NAME", stepName);
    }

    public void addRequest(String requestJson) {
        paramMap.put("STEP.REQUEST", requestJson);

    }

    public void addResponse(String responseJson) {
        paramMap.put("STEP.RESPONSE", responseJson);
    }

    public String getResolvedStep() {
        StrSubstitutor sub = new StrSubstitutor(paramMap);
        return sub.replace(requestResponseState);
    }
}
