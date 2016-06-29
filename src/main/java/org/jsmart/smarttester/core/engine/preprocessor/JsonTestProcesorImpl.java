package org.jsmart.smarttester.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.smarttester.core.engine.assertion.ArrayIsEmptyAsserter;
import org.jsmart.smarttester.core.engine.assertion.ArraySizeAsserter;
import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.jsmart.smarttester.core.engine.assertion.FieldHasExactValueAsserter;
import org.jsmart.smarttester.core.engine.assertion.FieldHasSubStringValueAsserter;
import org.jsmart.smarttester.core.engine.assertion.FieldIsNotNullAsserter;
import org.jsmart.smarttester.core.engine.assertion.FieldIsNullAsserter;
import org.jsmart.smarttester.core.engine.assertion.JsonAsserter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class JsonTestProcesorImpl implements JsonTestProcesor {

    private static final String RANDOM_IPT_STRING = "RANDOM_IPT_STRING:";
    private static final String IPT_REFERENCE_PREFIX = "ASU";
    private static final String RANDOM_X_REQUEST_ID = "X_REQUEST_ID";
    private static final String RANDOM_STRING_PREFIX = "RANDOM_STRING:";
    private static final String STATIC_ALPHABET = "STATIC_ALPHABET:";

    private static final List<String> availableTokens = Arrays.asList(
            RANDOM_IPT_STRING,
            IPT_REFERENCE_PREFIX,
            RANDOM_X_REQUEST_ID,
            RANDOM_STRING_PREFIX,
            STATIC_ALPHABET
    );

    private final ObjectMapper mapper;

    @Inject
    public JsonTestProcesorImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public String resolveRequestJson(String requestJsonAsString, String scenarioState) {
        Map<String, String> parammap = new HashMap<>();

        final List<String> allTokens = getAllTokens(requestJsonAsString);
        allTokens.forEach(runTimeToken -> {
            availableTokens.forEach(inStoreToken -> {
                if (runTimeToken.startsWith(inStoreToken)) {
                    if (runTimeToken.startsWith(RANDOM_IPT_STRING)) {
                        int length = Integer.parseInt(runTimeToken.substring(RANDOM_STRING_PREFIX.length()));
                        parammap.put(runTimeToken, createRandomAlphaString(length));

                    } else if (runTimeToken.startsWith(RANDOM_X_REQUEST_ID)) {
                        parammap.put(runTimeToken, System.currentTimeMillis() + "");

                    } else if (runTimeToken.startsWith(RANDOM_STRING_PREFIX)) {
                        int length = Integer.parseInt(runTimeToken.substring(RANDOM_STRING_PREFIX.length()));
                        parammap.put(runTimeToken, createRandomAlphaString(length));

                    } else if (runTimeToken.startsWith(STATIC_ALPHABET)) {
                        int length = Integer.parseInt(runTimeToken.substring(STATIC_ALPHABET.length()));
                        parammap.put(runTimeToken, createStaticAlphaString(length));

                    }
                }
            });
        });

        StrSubstitutor sub = new StrSubstitutor(parammap);
        String resolvedFromTemplate = sub.replace(requestJsonAsString);

        return resolveJsonPaths(resolvedFromTemplate, scenarioState);
    }

    @Override
    public List<String> getAllTokens(String requestJsonAsString) {
        List<String> keyTokens = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(requestJsonAsString);
        while (matcher.find()) {
            keyTokens.add(matcher.group(1));
        }

        return keyTokens;
    }

    @Override
    public String resolveJsonPaths(String jsonString, String scenarioState) {
        List<String> jsonPaths = getAllJsonPathTokens(jsonString);
        Map<String, String> paramMap = new HashMap<>();

        jsonPaths.forEach(thisPath -> {
            try {
                paramMap.put(thisPath, JsonPath.read(scenarioState, thisPath));
            } catch (Exception e) {
                throw new RuntimeException("\nJSON:" + jsonString + "\nPossibly comments in the JSON found or bad JSON path found: " + thisPath + ", Details: " + e);
            }
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(jsonString);
    }

    @Override
    public List<String> getAllJsonPathTokens(String requestJsonAsString) {
        List<String> jsonPaths = new ArrayList<>();

        final List<String> allTokens = getAllTokens(requestJsonAsString);
        allTokens.forEach(thisToken -> {
            if (thisToken.startsWith("$.")) {
                jsonPaths.add(thisToken);
            }
        });

        return jsonPaths;
    }

    @Override
    public List<JsonAsserter> createAssertersFrom(String resolvedAssertionJson) {
        List<JsonAsserter> asserters = new ArrayList<JsonAsserter>();
        try {
            JsonNode jsonNode = mapper.readTree(resolvedAssertionJson);

            Map<String, Object> createFieldsKeyValuesMap = createAssertionKV(jsonNode, "$.");

            int i = 1;
            for (Map.Entry<String, Object> entry : createFieldsKeyValuesMap.entrySet()) {
                String path = entry.getKey();
                Object value = entry.getValue();

                System.out.println(i++ + ": -------path:" + path + " -------value: " + value);

                JsonAsserter asserter;
                if ("$NOT_NULL".equals(value)) {
                    asserter = new FieldIsNotNullAsserter(path);
                }
                else if ("$NULL".equals(value)) {
                    asserter = new FieldIsNullAsserter(path);
                } else if ("$[]".equals(value)) {
                    asserter = new ArrayIsEmptyAsserter(path);
                } else if (path.endsWith(".SIZE")) {
                    path = path.substring(0, path.length() - ".SIZE".length());
                    asserter = new ArraySizeAsserter(path, ((Integer) value).intValue());
                } else if (value instanceof String && ((String) value).startsWith("$CONTAINS_STRING:")) {
                    String expected = ((String) value).substring("$CONTAINS_STRING:".length());
                    asserter = new FieldHasSubStringValueAsserter(path, expected);
                } else {
                    //TODO
                    asserter = new FieldHasExactValueAsserter(path, value);
                }

                asserters.add(asserter);
            }
        } catch (IOException parEx) {
            throw new RuntimeException(parEx);
        }

        return asserters;
    }

    private Map<String, Object> createAssertionKV(JsonNode jsonNode, String pathDslPrefix) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        if (jsonNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            jsonNode.fieldNames().forEachRemaining(fieldName -> {
                String qualifiedName = pathDslPrefix + fieldName;
                JsonNode thisNode = jsonNode.get(fieldName);

                if (thisNode.isValueNode()) {
                    Object value = convertJsonTypeToJavaType(jsonNode.get(fieldName));
                    result.put(qualifiedName, value);

                } else {
                    String newPrefix = qualifiedName + ".";
                    result.putAll(createAssertionKV(thisNode, newPrefix));

                }
            });

        } else if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
            int i = 0;
            final Iterator<JsonNode> arrayIterator = jsonNode.elements();
            while (arrayIterator.hasNext()) {
                final JsonNode thisElementValue = arrayIterator.next();
                String elementName = String.format("%s[%d]", pathDslPrefix.substring(0, pathDslPrefix.lastIndexOf('.')), i++);

                if (thisElementValue.isValueNode()) {
                    Object value = convertJsonTypeToJavaType(thisElementValue);
                    result.put(elementName, value);

                } else {
                    String newPrefix = elementName + ".";
                    result.putAll(createAssertionKV(thisElementValue, newPrefix));

                }
            }
        } else {
            throw new RuntimeException(format("Oops! Unsupported JSON Type: %s", jsonNode.getClass().getName()));

        }

        return result;
    }

    private Object convertJsonTypeToJavaType(JsonNode jsonNode) {
        if (jsonNode.isValueNode()) {
            if(jsonNode.isInt()){
                return jsonNode.asInt();

            } else if(jsonNode.isTextual()){
                return jsonNode.asText();

            } else if(jsonNode.isBoolean()){
                return jsonNode.asBoolean();

            } else if(jsonNode.isLong()){
                return jsonNode.asLong();

            } else if(jsonNode.isDouble()){
                return jsonNode.asDouble();

            } else if(jsonNode.isNull()){
                return null;

            } else {
                throw new RuntimeException(format("Oops! Unsupported JSON primitive to Java : %s by the framework", jsonNode.getClass().getName()));
            }
        } else {
            throw new RuntimeException(format("Unsupported JSON Type: %s", jsonNode.getClass().getName()));
        }
    }

    @Override
    public List<AssertionReport> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult) {

        List<AssertionReport> failedReports = new ArrayList<>();

        asserters.forEach(asserter -> {

            final AssertionReport assertionReport = asserter.assertWithJson(executionResult);

            if (!assertionReport.matches()) {

                failedReports.add(assertionReport);

            }
        });

        return failedReports;
    }

    private String createRandomAlphaString(int length) {
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('A' + r.nextInt(26)));
        }
        String randomString = builder.toString();
        return randomString;
    }

    private String createRandomIPTString(int length) {
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('0' + r.nextInt(10)));
        }
        String randomString = builder.toString();
        return randomString;
    }

    private String createStaticAlphaString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('A' + i));

            /*
             * This will repeat after A to Z
             */
            i = i >= 26 ? 0 : i;
        }

        return builder.toString();
    }

    public static List<String> getAvailableTokens() {
        return availableTokens;
    }
}
