package org.jsmart.zerocode.integrationtests.urlencoded;

import java.util.HashMap;
import java.util.Map;

/**
 * Hands over an id containing special characters, e.g. "#37:29", to the next step of a
 * scenario, then echoes back the url built by that step. Used by the URL encoding tests.
 */
public class QuestionService {

    public Map<String, String> createQuestion() {
        Map<String, String> question = new HashMap<>();
        question.put("id", "#37:29");

        return question;
    }

    public Map<String, String> echoUrl(Map<String, String> request) {
        return request;
    }
}
