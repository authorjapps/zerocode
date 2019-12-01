package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.array.ArrayIsEmptyAsserterImpl;
import org.jsmart.zerocode.core.engine.assertion.array.ArraySizeAsserterImpl;
import org.jsmart.zerocode.core.engine.assertion.field.FieldContainsStringAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldContainsStringIgnoreCaseAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasDateAfterValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasDateBeforeValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasEqualNumberValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasExactValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasGreaterThanValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasInEqualNumberValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldHasLesserThanValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldIsNotNullAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldIsNullAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldIsOneOfValueAsserter;
import org.jsmart.zerocode.core.engine.assertion.field.FieldMatchesRegexPatternAsserter;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.substringBetween;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_LOCAL_DATETIME_AFTER;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_LOCAL_DATETIME_BEFORE;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_PATH_SIZE;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_CONTAINS_STRING;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_EMPTY_ARRAY;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_EQUAL_TO_NUMBER;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_GREATER_THAN;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_IS_NOT_NULL;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_IS_NULL;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_IS_ONE_OF;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_LESSER_THAN;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_MATCHES_STRING;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_NOT_EQUAL_TO_NUMBER;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_NOT_NULL;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_NULL;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.ASSERT_VALUE_ONE_OF;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeAssertionTokens.RAW_BODY;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.$VALUE;
import static org.jsmart.zerocode.core.utils.FieldTypeConversionUtils.digTypeCast;
import static org.jsmart.zerocode.core.utils.FieldTypeConversionUtils.fieldTypes;
import static org.jsmart.zerocode.core.utils.PropertiesProviderUtils.loadAbsoluteProperties;
import static org.jsmart.zerocode.core.utils.SmartUtils.isValidAbsolutePath;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;
import static org.jsmart.zerocode.core.utils.TokenUtils.populateParamMap;
import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeAssertionsProcessorImpl implements ZeroCodeAssertionsProcessor {

    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeAssertionsProcessorImpl.class);

    final List<String> propertyKeys = new ArrayList<>();
    final Properties properties = new Properties();

    private final ObjectMapper mapper;
    private final String hostFileName;

    @Inject
    public ZeroCodeAssertionsProcessorImpl(ObjectMapper mapper, @Named("HostFileName") String hostFileName) {
        this.mapper = mapper;
        this.hostFileName = hostFileName;
        loadAnnotatedHostProperties();
    }

    @Override
    public String resolveStringJson(String requestJsonOrAnyString, String scenarioStateJson) {
        String resolvedFromTemplate = resolveKnownTokensAndProperties(requestJsonOrAnyString);
        String resolvedJson = resolveJsonPaths(resolvedFromTemplate, scenarioStateJson);
        return resolveFieldTypes(resolvedJson);
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
        final String LEAF_VAL_REGEX = "\\$[.](.*)\\$VALUE\\[\\d\\]";

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

                } else if (thisPath.matches(LEAF_VAL_REGEX) || thisPath.endsWith($VALUE)) {

                    resolveLeafOnlyNodeValue(scenarioState, paramMap, thisPath);

                } else {
                    Object jsonPathValue = JsonPath.read(scenarioState, thisPath);
                    if (isPathValueJson(jsonPathValue)) {
                        final String jsonAsString = mapper.writeValueAsString(jsonPathValue);
                        String escapedJsonString = escapeJava(jsonAsString);
                        paramMap.put(thisPath, escapedJsonString);

                    } else {

                        paramMap.put(thisPath, JsonPath.read(scenarioState, thisPath));

                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("\nJSON:" + jsonString + "\nPossibly comments in the JSON found or bad JSON path found: " + thisPath + ",\nDetails: " + e);
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
    public List<JsonAsserter> createJsonAsserters(String resolvedAssertionJson) {
        List<JsonAsserter> asserters = new ArrayList<>();
        try {
            JsonNode jsonNode = mapper.readTree(resolvedAssertionJson);

            Map<String, Object> createFieldsKeyValuesMap = createAssertionKV(jsonNode, "$.");

            int i = 1;
            for (Map.Entry<String, Object> entry : createFieldsKeyValuesMap.entrySet()) {
                String path = entry.getKey();
                Object value = entry.getValue();

                JsonAsserter asserter;
                if (ASSERT_VALUE_NOT_NULL.equals(value) || ASSERT_VALUE_IS_NOT_NULL.equals(value)) {
                    asserter = new FieldIsNotNullAsserter(path);
                } else if (ASSERT_VALUE_NULL.equals(value) || ASSERT_VALUE_IS_NULL.equals(value)) {
                    asserter = new FieldIsNullAsserter(path);
                } else if (ASSERT_VALUE_EMPTY_ARRAY.equals(value)) {
                    asserter = new ArrayIsEmptyAsserterImpl(path);
                } else if (path.endsWith(ASSERT_PATH_SIZE)) {
                    path = path.substring(0, path.length() - ASSERT_PATH_SIZE.length());
                    if (value instanceof Number) {
                        asserter = new ArraySizeAsserterImpl(path, ((Integer) value).intValue());
                    } else if (value instanceof String) {
                        asserter = new ArraySizeAsserterImpl(path, (String) value);
                    } else {
                        throw new RuntimeException(format("Oops! Unsupported value for .SIZE: %s", value));
                    }
                } else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_CONTAINS_STRING)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_CONTAINS_STRING.length());
                    asserter = new FieldContainsStringAsserter(path, expected);
                } else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_MATCHES_STRING)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_MATCHES_STRING.length());
                    asserter = new FieldMatchesRegexPatternAsserter(path, expected);
                } else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE.length());
                    asserter = new FieldContainsStringIgnoreCaseAsserter(path, expected);
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasEqualNumberValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasInEqualNumberValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_GREATER_THAN)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_GREATER_THAN.length());
                    asserter = new FieldHasGreaterThanValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_LESSER_THAN)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_LESSER_THAN.length());
                    asserter = new FieldHasLesserThanValueAsserter(path, numberValueOf(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_LOCAL_DATETIME_AFTER)) {
                    String expected = ((String) value).substring(ASSERT_LOCAL_DATETIME_AFTER.length());
                    asserter = new FieldHasDateAfterValueAsserter(path, parseLocalDateTime(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_LOCAL_DATETIME_BEFORE)) {
                    String expected = ((String) value).substring(ASSERT_LOCAL_DATETIME_BEFORE.length());
                    asserter = new FieldHasDateBeforeValueAsserter(path, parseLocalDateTime(expected));
                } else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_ONE_OF) ||
                        value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_IS_ONE_OF)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_ONE_OF.length());
                    asserter = new FieldIsOneOfValueAsserter(path, expected);
                } else {
                    asserter = new FieldHasExactValueAsserter(path, value);
                }

                asserters.add(asserter);
            }
        } catch (IOException parEx) {
            throw new RuntimeException(parEx);
        }

        return asserters;
    }

    private BigDecimal numberValueOf(String expected) {
        try {
            return new BigDecimal(expected);
        } catch (Exception e) {
            String msg = "\nValue '" + expected + "' can not be converted to number:" + e;
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private Map<String, Object> createAssertionKV(JsonNode jsonNode, String pathDslPrefix) {
        HashMap<String, Object> resultMap = new HashMap<>();

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
    public List<FieldAssertionMatcher> assertAllAndReturnFailed(List<JsonAsserter> asserters, String executionResult) {

        List<FieldAssertionMatcher> failedReports = new ArrayList<>();

        asserters.forEach(asserter -> {

            final FieldAssertionMatcher fieldMatcher = asserter.assertWithJson(executionResult);

            if (!fieldMatcher.matches()) {

                failedReports.add(fieldMatcher);

            }
        });

        return failedReports;
    }

    private void loadAnnotatedHostProperties() {
        try {
            if(isValidAbsolutePath(hostFileName)){
               loadAbsoluteProperties(hostFileName, properties);
            } else {
                properties.load(getClass().getClassLoader().getResourceAsStream(hostFileName));
            }

        } catch (Exception e) {
            String msg = "Problem encountered while accessing annotated host properties file '";
            LOGGER.error(msg + hostFileName + "'");
            System.err.println(msg + hostFileName + "'");
            throw new RuntimeException(msg + e);
        }

        properties.keySet().stream().forEach(thisKey -> {
            propertyKeys.add(thisKey.toString());
        });
    }

    private boolean isPropertyKey(String runTimeToken) {
        return propertyKeys.contains(runTimeToken);
    }

    private LocalDateTime parseLocalDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }

    boolean isPathValueJson(Object jsonPathValue) {
        return jsonPathValue instanceof LinkedHashMap || jsonPathValue instanceof JSONArray;
    }

    void resolveLeafOnlyNodeValue(String scenarioState, Map<String, String> paramMap, String thisPath) {
        String actualPath = thisPath.substring(0, thisPath.indexOf($VALUE));
        int index = findArrayIndex(thisPath, actualPath);

        List<String> leafValuesAsArray = JsonPath.read(scenarioState, actualPath);
        paramMap.put(thisPath, leafValuesAsArray.get(index));
    }

    private int findArrayIndex(String thisPath, String actualPath) {
        String valueExpr = thisPath.substring(actualPath.length());
        if ($VALUE.equals(valueExpr)) {
            return 0;
        }
        return valueOf(substringBetween(valueExpr, "[", "]"));
    }

    private String resolveFieldTypes(String resolvedJson) {
        try {
            if (hasNoTypeCast(resolvedJson)) {
                return resolvedJson;
            }

            Map<String, Object> fieldMap = mapper.readValue(resolvedJson, new TypeReference<Map<String, Object>>() { });
            digTypeCast(fieldMap);

            return mapper.writeValueAsString(fieldMap);

        } catch (Exception ex) {
            LOGGER.error("Field Type conversion exception. \nDetails:" + ex);
            throw new RuntimeException(ex);
        }
    }

    private boolean hasNoTypeCast(String resolvedJson) {
        long foundCount = fieldTypes.stream().filter(thisType -> resolvedJson.contains(thisType)).count();
        return foundCount <= 0 ? true : false;
    }


}
