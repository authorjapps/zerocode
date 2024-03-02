package org.jsmart.zerocode.core.engine.preprocessor;

import org.jsmart.zerocode.TestUtility;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class StepExecutionStateTest {
    StepExecutionState stepExecutionState;

    @Before
    public void setUpStuff() throws Exception {
        stepExecutionState = new StepExecutionState();
    }

    @Test
    public void willHaveReqResp_resolved() throws Exception {

        StepExecutionState stepExecutionState = new StepExecutionState();
        stepExecutionState.addStep(TestUtility.createDummyStep("Step-1"));
        stepExecutionState.addRequest("{\n" +
                "    \"customer\": {\n" +
                "        \"firstName\": \"FIRST_NAME\"\n" +
                "    }\n" +
                "}");
        stepExecutionState.addResponse("{\n" +
                "    \"id\" : 10101\n" +
                "}");

        JSONAssert.assertEquals(String.format("{%s}", stepExecutionState.getResolvedStep()), "{\n" +
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
    }

    @Test
    public void willBeAbleToAdd_RequestAndResponse() throws Exception {
        stepExecutionState.addStep(TestUtility.createDummyStep("Step-X1"));
        stepExecutionState.addRequest("{\n" +
                "    \"customer\": {\n" +
                "        \"firstName\": \"FIRST_NAME\"\n" +
                "    }\n" +
                "}");
        stepExecutionState.addResponse("{\n" +
                "    \"id\" : 10101\n" +
                "}");

        String resolvedString = stepExecutionState.getResolvedStep();

        JSONAssert.assertEquals(String.format("{%s}", resolvedString), "{\n" +
                "    \"Step-X1\": {\n" +
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
}