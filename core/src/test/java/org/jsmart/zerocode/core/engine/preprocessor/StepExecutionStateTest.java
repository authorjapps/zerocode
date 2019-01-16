package org.jsmart.zerocode.core.engine.preprocessor;

import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

public class StepExecutionStateTest {
    StepExecutionState stepExecutionState;

    @Before
    public void setUpStuff() throws Exception {
        stepExecutionState = new StepExecutionState();
    }

    @Test
    public void willHaveReqResp_resolved() throws Exception {

        Map<String, String> parammap = new HashMap<>();

        parammap.put("STEP.NAME", "Step-1");
        parammap.put("STEP.REQUEST", "{\n" +
                "    \"customer\": {\n" +
                "        \"firstName\": \"FIRST_NAME\"\n" +
                "    }\n" +
                "}");
        parammap.put("STEP.RESPONSE", "{\n" +
                "    \"id\" : 10101\n" +
                "}");

        StrSubstitutor sub = new StrSubstitutor(parammap);
        String resolvedString = sub.replace(stepExecutionState.getRequestResponseState());

        JSONAssert.assertEquals(String.format("{%s}", resolvedString), "{\n" +
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
    public void willBeAbleToAdd_RequestAndReponse() throws Exception {
        stepExecutionState.addStep("Step-X1");
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