package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.TestUtility;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ScenarioExecutionStateTest {

    ScenarioExecutionState scenarioExecutionState;

    @Before
    public void initializeStuff() throws Exception {
        scenarioExecutionState = new ScenarioExecutionState();
    }

    @Test
    public void willRecordStep1AndStep2_Req_Resp() throws Exception {
        JSONAssert.assertEquals(scenarioExecutionState.getResolvedScenarioState(), "{}", true);


        final StepExecutionState step1 = createStepWith("Step-1");
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

        final StepExecutionState step2 = createStepWith("Step-2");
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

    protected StepExecutionState createStepWith(String stepName) {
        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(TestUtility.createDummyStep(stepName));
        stepExecutionState.addRequest("{\n" +
                "    \"customer\": {\n" +
                "        \"firstName\": \"FIRST_NAME\"\n" +
                "    }\n" +
                "}");
        stepExecutionState.addResponse("{\n" +
                "    \"id\" : 10101\n" +
                "}");
        return stepExecutionState;
    }


}