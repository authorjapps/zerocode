package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

// TODO: Move this to Smartutils class
public class HelperJsonUtils {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HelperJsonUtils.class);
    private static ObjectMapper mapper = new ObjectMapperProvider().get();


    public static String getContentAsItIsJson(Object bodyContent) {

        if (null == bodyContent) {
            return null;
        }

        final JsonNode bodyJsonNode;
        try {
            /*
             * The bodyContent is a map, because it was read uisng jayway-jaon-path.
             * So needed to be converted into json string.
             */
            final String bodyContentAsString = mapper.writeValueAsString(bodyContent);
            bodyJsonNode = mapper.readValue(bodyContentAsString, JsonNode.class);

            if (bodyJsonNode.isValueNode()) {
                return bodyJsonNode.asText();
            }

            if (bodyJsonNode.size() == 0) {
                return null;
            }

            return bodyJsonNode.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static Map<String, Object> readObjectAsMap(Object jsonContent) {
        Map<String, Object> map;
        try {
            map = mapper.readValue(jsonContent.toString(), HashMap.class);
        } catch (IOException exx) {
            LOGGER.error("Exception occurred during parse to HashMap - " + exx);
            throw new RuntimeException(exx);
        }

        return map;
    }

    public static String createAndReturnAssertionResultJson(int httpResponseCode,
                                                            String resultBodyContent, String locationHref) {
        LOGGER.debug("\n#locationHref: " + locationHref);

        if (StringUtils.isEmpty(resultBodyContent)) {
            resultBodyContent = "{}";
        }
        String locationField = locationHref != null ? "	\"Location\" : \"" + locationHref + "\",\n" : "";
        String assertJson = "{\n" +
                "	\"status\" : " + httpResponseCode + ",\n" +
                locationField +
                "	\"body\" : " + resultBodyContent + "\n" +
                " }";

        String formattedStr = SmartUtils.prettyPrintJson(assertJson);

        return formattedStr;
    }

    public static String javaObjectAsString(Object value) {

        try {
            ObjectMapper ow = new ObjectMapperProvider().get();
            return ow.writeValueAsString(value);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while converting IPT Java Object to JsonString" + e);
        }
    }


    public static List<FieldAssertionMatcher> strictComparePayload(String expectedResult, String actualResult) {
        List<FieldAssertionMatcher> matchers = new ArrayList<>();

        String actualResultJson = readPayload(actualResult);
        String expectedResultJson = readPayload(expectedResult);
        try {
            assertEquals(expectedResultJson, actualResultJson, STRICT);
        } catch (AssertionError scream) {
            String rawMsg = scream.getMessage();
            List<String> messageList = Arrays.asList(rawMsg.split(";"));
            matchers = messageList.stream()
                    .map(msg -> {
                        List<String> strings = Arrays.asList(msg.trim().split("\n"));
                        String fieldJsonPath = "";
                        if(strings != null && strings.size() > 0){
                            fieldJsonPath = strings.get(0).substring(strings.get(0).indexOf(": ") + 1).trim();
                        }
                        if (strings.size() == 1) {
                            return aNotMatchingMessage(fieldJsonPath, "", strings.get(0).trim());
                        } else if (strings.size() == 2) {
                            return aNotMatchingMessage(fieldJsonPath, "", strings.get(1).trim());
                        } else if (strings.size() > 2) {
                            return aNotMatchingMessage(fieldJsonPath, strings.get(1).trim(), strings.get(2).trim());
                        } else {
                            return aMatchingMessage();
                        }
                    })
                    .collect(Collectors.toList());
        }

        return matchers;
    }

    private static String readPayload(String json) {
        String bodyPath = "$.body";
        String rawBodyPath = "$.rawBody";

        Map payload = (Map) readJsonPathOrElseNull(json, bodyPath);
        payload = payload == null ? (Map) readJsonPathOrElseNull(json, rawBodyPath) : payload;

        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Exception while reading payload - " + ex);
            throw new RuntimeException(ex);
        }
    }

    public static Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.warn("No " + jsonPath + " was present in the request. returned null.");
            return null;
        }
    }
}
