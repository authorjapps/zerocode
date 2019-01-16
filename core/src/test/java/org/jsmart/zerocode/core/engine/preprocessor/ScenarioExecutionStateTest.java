package org.jsmart.zerocode.core.engine.preprocessor;

import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

public class ScenarioExecutionStateTest {

    ScenarioExecutionState scenarioExecutionState;

    @Before
    public void initializeSTuff() throws Exception {
        scenarioExecutionState = new ScenarioExecutionState();
    }

    @Test
    public void willRecordStep1AndStep2_Req_Resp() throws Exception {
        JSONAssert.assertEquals(scenarioExecutionState.getResolvedScenarioState(), "{}", true);


        final String step1 = createStepWith("Step-1");
        scenarioExecutionState.addStepState(step1);
        JSONAssert.assertEquals(scenarioExecutionState.getResolvedScenarioState(), "{\n" +
                "    \"Step-1\": {\n" +
                "        \"request\": {\n" +
                "            \"customer\": {\n" +
                "                \"firstName\": \"FIRST_NAME\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": {\n" +
                "            \"id\": 10101\n" +
                "        }\n" +
                "    }\n" +
                "}", true);

        final String step2 = createStepWith("Step-2");
        scenarioExecutionState.addStepState(step2);
        final String resolvedScene = scenarioExecutionState.getResolvedScenarioState();
        JSONAssert.assertEquals(resolvedScene, "{\n" +
                "    \"Step-1\": {\n" +
                "        \"request\": {\n" +
                "            \"customer\": {\n" +
                "                \"firstName\": \"FIRST_NAME\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": {\n" +
                "            \"id\": 10101\n" +
                "        }\n" +
                "    },\n" +
                "    \"Step-2\": {\n" +
                "        \"request\": {\n" +
                "            \"customer\": {\n" +
                "                \"firstName\": \"FIRST_NAME\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"response\": {\n" +
                "            \"id\": 10101\n" +
                "        }\n" +
                "    }\n" +
                "}", true);
    }

    protected String createStepWith(String stepName) {
        Map<String, String> parammap = new HashMap<>();

        parammap.put("STEP.NAME", stepName);
        parammap.put("STEP.REQUEST", "{\n" +
                "    \"customer\": {\n" +
                "        \"firstName\": \"FIRST_NAME\"\n" +
                "    }\n" +
                "}");
        parammap.put("STEP.RESPONSE", "{\n" +
                "    \"id\" : 10101\n" +
                "}");

        StrSubstitutor sub = new StrSubstitutor(parammap);

        return sub.replace((new StepExecutionState()).getRequestResponseState());
    }


}