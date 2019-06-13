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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;

import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.domain.reports.LocalDateTimeDeserializer;
import org.jsmart.zerocode.core.engine.assertion.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.String.format;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.jsmart.zerocode.core.utils.TokenUtils.*;
import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeJsonTestProcesorImpl implements ZeroCodeJsonTestProcesor {

    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeJsonTestProcesorImpl.class);

    final List<String> propertyKeys = new ArrayList<>();
    final Properties properties = new Properties();

    // -----------------------
    // Assertion place holders
    // -----------------------
    public static final String ASSERT_VALUE_NOT_NULL = "$NOT.NULL";
    public static final String ASSERT_VALUE_NULL = "$NULL";
    public static final String ASSERT_VALUE_EMPTY_ARRAY = "$[]";
    public static final String ASSERT_PATH_SIZE = ".SIZE";
    public static final String ASSERT_VALUE_CONTAINS_STRING = "$CONTAINS.STRING:";
    public static final String ASSERT_VALUE_MATCHES_STRING = "$MATCHES.STRING:";
    public static final String ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE = "$CONTAINS.STRING.IGNORECASE:";
    public static final String ASSERT_VALUE_EQUAL_TO_NUMBER = "$EQ.";
    public static final String ASSERT_VALUE_NOT_EQUAL_TO_NUMBER = "$NOT.EQ.";
    public static final String ASSERT_VALUE_GREATER_THAN = "$GT.";
    public static final String ASSERT_VALUE_LESSER_THAN = "$LT.";
    public static final String ASSERT_DATE_AFTER = "$DATE.AFTER:";
    public static final String ASSERT_DATE_BEFORE = "$DATE.BEFORE:";
    public static final String ASSERT_PATH_VALUE_NODE = "$";
    public static final String RAW_BODY = ".rawBody";

    private final ObjectMapper mapper;
    private final String hostFileName;

    @Inject
    public ZeroCodeJsonTestProcesorImpl(ObjectMapper mapper, @Named("HostFileName") String hostFileName) {
        this.mapper = mapper;
        this.hostFileName = hostFileName;
        loadAnnotatedHostProperties();
    }

    @Override
    public String resolveStringJson(String requestJsonOrAnyString, String scenarioStateJson) {
        String resolvedFromTemplate = resolveKnownTokensAndProperties(requestJsonOrAnyString);
        return resolveJsonPaths(resolvedFromTemplate, scenarioStateJson);
    }

    @Override
    public String resolveKnownTokensAndProperties(String requestJsonOrAnyString) {
        Map<String, Object> paramMap = new HashMap<>();

        final List<String> testCaseTokens = getTestCaseTokens(requestJsonOrAnyString);

        testCaseTokens.forEach(runTimeToken -> {
            populateParamMap(paramMap, runTimeToken);

            if (isPropertyKey(runTimeToken)) {
                paramMap.put(runTimeToken, properties.get(runTimeToken));
            }

        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(requestJsonOrAnyString);
    }

    @Override
    public String resolveJsonPaths(String jsonString, String scenarioState) {
        List<String> jsonPaths = getAllJsonPathTokens(jsonString);
        Map<String, String> paramMap = new HashMap<>();

        jsonPaths.forEach(thisPath -> {
            try {

                if (thisPath.endsWith(RAW_BODY)) {
                    /**
                     * In case the rawBody is used anywhere in the steps as $.step_name.response.rawBody,
                     * then it must be escaped as the content was not a simple JSON string to be able
                     * to convert to json. Hence without throwing exception, treat as string content.
                     *
                     * Use escapeJava, do not use escapeJavaScript, as escapeJavaScript also escapes single quotes
                     * which in turn throws Jackson Exception
                     */
                    String escapedString = escapeJava(JsonPath.read(scenarioState, thisPath));
                    paramMap.put(thisPath, escapedString);

                } else {
                    // if it is a json block/node or array, this return value is LinkedHashMap.
                    if (JsonPath.read(scenarioState, thisPath) instanceof LinkedHashMap) {
                        final String pathValue = mapper.writeValueAsString(JsonPath.read(scenarioState, thisPath));
                        String escapedPathValue = escapeJava(pathValue);
                        paramMap.put(thisPath, escapedPathValue);

                    } else {
                        // Usual flow
                        paramMap.put(thisPath, JsonPath.read(scenarioState, thisPath));

                    }

                }

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

        final List<String> allTokens = getTestCaseTokens(requestJsonAsString);
        allTokens.forEach(thisToken -> {
            if (thisToken.startsWith("$.")) {
                jsonPaths.add(thisToken);
            }
        });

        return jsonPaths;
    }

    @Override
    public List<JsonAsserter> createAssertersFrom(String resolvedAssertionJson) {
        List<JsonAsserter> asserters = new ArrayList<>();
        try {
            JsonNode jsonNode = mapper.readTree(resolvedAssertionJson);

            Map<String, Object> createFieldsKeyValuesMap = createAssertionKV(jsonNode, "$.");

            int i = 1;
            for (Map.Entry<String, Object> entry : createFieldsKeyValuesMap.entrySet()) {
                String path = entry.getKey();
                Object value = entry.getValue();

                JsonAsserter asserter;
                if (ASSERT_VALUE_NOT_NULL.equals(value)) {
                    asserter = new FieldIsNotNullAsserter(path);
                } else if (ASSERT_VALUE_NULL.equals(value)) {
                    asserter = new FieldIsNullAsserter(path);
                } else if (ASSERT_VALUE_EMPTY_ARRAY.equals(value)) {
                    asserter = new ArrayIsEmptyAsserter(path);
                } else if (path.endsWith(ASSERT_PATH_SIZE)) {
                    path = path.substring(0, path.length() - ASSERT_PATH_SIZE.length());
                    if (value instanceof Number) {
                        asserter = new ArraySizeAsserter(path, ((Integer) value).intValue());
                    } else if (value instanceof String) {
                        asserter = new ArraySizeAsserter(path, (String) value);
                    } else {
                        throw new RuntimeException(format("Oops! Unsupported value for .SIZE: %s", value));
                    }
                } else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_CONTAINS_STRING)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_CONTAINS_STRING.length());
                    asserter = new FieldHasSubStringValueAsserter(path, expected);
                } else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_MATCHES_STRING)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_MATCHES_STRING.length());
                    asserter = new FieldMatchesRegexPatternAsserter(path, expected);
                } else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE.length());
                    asserter = new FieldHasSubStringIgnoreCaseValueAsserter(path, expected);
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasEqualNumberValueAsserter(path, new BigDecimal(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasInEqualNumberValueAsserter(path, new BigDecimal(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_GREATER_THAN)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_GREATER_THAN.length());
                    asserter = new FieldHasGreaterThanValueAsserter(path, new BigDecimal(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_LESSER_THAN)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_LESSER_THAN.length());
                    asserter = new FieldHasLesserThanValueAsserter(path, new BigDecimal(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_DATE_AFTER)) {
                    String expected = ((String) value).substring(ASSERT_DATE_AFTER.length());
                    asserter = new FieldHasDateAfterValueAsserter(path, parseLocalDateTime(expected));
                }else if (value instanceof String && (value.toString()).startsWith(ASSERT_DATE_BEFORE)) {
                    String expected = ((String) value).substring(ASSERT_DATE_BEFORE.length());
                    asserter = new FieldHasDateBeforeValueAsserter(path, parseLocalDateTime(expected));
                }
                else {
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
        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        if (jsonNode.getNodeType().equals(JsonNodeType.OBJECT)) {
            jsonNode.fieldNames().forEachRemaining(fieldName -> {
                String qualifiedName = pathDslPrefix + fieldName;
                JsonNode thisNode = jsonNode.get(fieldName);

                if (thisNode.isValueNode()) {
                    Object value = convertJsonTypeToJavaType(jsonNode.get(fieldName));
                    resultMap.put(qualifiedName, value);

                } else {
                    String newPrefix = qualifiedName + ".";
                    resultMap.putAll(createAssertionKV(thisNode, newPrefix));

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
                    resultMap.put(elementName, value);

                } else {
                    String newPrefix = elementName + ".";
                    resultMap.putAll(createAssertionKV(thisElementValue, newPrefix));

                }
            }
        }
        /*
         * This is useful when only value is present without a key
         * i.e. still a valid JSON even if it doesnt start-end with a '{'
         */
        else if (jsonNode.isValueNode()) {
            Object value = convertJsonTypeToJavaType(jsonNode);
            resultMap.put("$", value);

        } else {
            throw new RuntimeException(format("Oops! Unsupported JSON Type: %s", jsonNode.getClass().getName()));

        }

        return resultMap;
    }

    private Object convertJsonTypeToJavaType(JsonNode jsonNode) {
        if (jsonNode.isValueNode()) {
            if (jsonNode.isInt()) {
                return jsonNode.asInt();

            } else if (jsonNode.isTextual()) {
                return jsonNode.asText();

            } else if (jsonNode.isBoolean()) {
                return jsonNode.asBoolean();

            } else if (jsonNode.isLong()) {
                return jsonNode.asLong();

            } else if (jsonNode.isDouble()) {
                return jsonNode.asDouble();

            } else if (jsonNode.isNull()) {
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

    private void loadAnnotatedHostProperties() {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(hostFileName));
        } catch (IOException e) {
            LOGGER.error("Problem encountered while accessing annotated host properties file '" + hostFileName + "'");
            throw new RuntimeException(e);
        }

        properties.keySet().stream().forEach(thisKey -> {
            propertyKeys.add(thisKey.toString());
        });
    }

    private boolean isPropertyKey(String runTimeToken) {
        return propertyKeys.contains(runTimeToken);
    }
    
    private LocalDateTime parseLocalDateTime (String value){ 	
    	return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }
}
