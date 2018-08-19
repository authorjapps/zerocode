package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.engine.assertion.*;
import org.jsmart.zerocode.core.utils.SmartUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeJsonTestProcesorImpl implements ZeroCodeJsonTestProcesor {

    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeJsonTestProcesorImpl.class);

    /*
     * General place holders
     */
    private static final String PREFIX_ASU = "ASU";
    private static final String RANDOM_NUMBER = "RANDOM.NUMBER";
    private static final String RANDOM_STRING_PREFIX = "RANDOM.STRING:";
    private static final String STATIC_ALPHABET = "STATIC.ALPHABET:";
    private static final String LOCALDATE_TODAY = "LOCAL.DATE.TODAY:";
    private static final String LOCALDATETIME_NOW = "LOCAL.DATETIME.NOW:";
    private static final String XML_FILE = "XML.FILE:";
    private static final String RANDOM_UU_ID = "RANDOM.UUID";

    private static final List<String> availableTokens = Arrays.asList(
            PREFIX_ASU,
            RANDOM_NUMBER,
            RANDOM_STRING_PREFIX,
            STATIC_ALPHABET,
            LOCALDATE_TODAY,
            LOCALDATETIME_NOW,
            XML_FILE,
            RANDOM_UU_ID
    );

    /*
     * Assertion place holders
     */
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
    public static final String ASSERT_PATH_VALUE_NODE = "$";
    private static final String RAW_BODY = ".rawBody";

    private final ObjectMapper mapper;

    @Inject
    public ZeroCodeJsonTestProcesorImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public String resolveStringJson(String requestJsonOrAnyString, String scenarioStateJson) {
        Map<String, String> parammap = new HashMap<>();

        final List<String> allTokens = getAllTokens(requestJsonOrAnyString);
        allTokens.forEach(runTimeToken -> {
            availableTokens.forEach(inStoreToken -> {
                if (runTimeToken.startsWith(inStoreToken)) {
                    if (runTimeToken.startsWith(RANDOM_NUMBER)) {
                        parammap.put(runTimeToken, System.currentTimeMillis() + "");

                    } else if (runTimeToken.startsWith(RANDOM_STRING_PREFIX)) {
                        int length = Integer.parseInt(runTimeToken.substring(RANDOM_STRING_PREFIX.length()));
                        parammap.put(runTimeToken, createRandomAlphaString(length));

                    } else if (runTimeToken.startsWith(STATIC_ALPHABET)) {
                        int length = Integer.parseInt(runTimeToken.substring(STATIC_ALPHABET.length()));
                        parammap.put(runTimeToken, createStaticAlphaString(length));

                    } else if (runTimeToken.startsWith(LOCALDATE_TODAY)) {
                        String formatPattern = runTimeToken.substring(LOCALDATE_TODAY.length());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
                        parammap.put(runTimeToken, LocalDate.now().format(formatter));

                    } else if (runTimeToken.startsWith(LOCALDATETIME_NOW)) {
                        String formatPattern = runTimeToken.substring(LOCALDATETIME_NOW.length());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
                        parammap.put(runTimeToken, LocalDateTime.now().format(formatter));
                    } else if (runTimeToken.startsWith(XML_FILE)) {
                        String xmlFileResource = runTimeToken.substring(XML_FILE.length());
                        final String xmlString = getXmlContent(xmlFileResource);
                        // Used escapeJava, do not use escapeXml as it replaces
                        // with GT LT etc ie what exactly you don't want
                        parammap.put(runTimeToken, escapeJava(xmlString));

                    } else if (runTimeToken.startsWith(RANDOM_UU_ID)) {
                        parammap.put(runTimeToken, randomUUID().toString());
                    }
                }
            });
        });

        StrSubstitutor sub = new StrSubstitutor(parammap);
        String resolvedFromTemplate = sub.replace(requestJsonOrAnyString);

        return resolveJsonPaths(resolvedFromTemplate, scenarioStateJson);
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

                if(thisPath.endsWith(RAW_BODY)){
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
                    if(JsonPath.read(scenarioState, thisPath) instanceof LinkedHashMap){
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
                }
                else if (ASSERT_VALUE_NULL.equals(value)) {
                    asserter = new FieldIsNullAsserter(path);
                }
                else if (ASSERT_VALUE_EMPTY_ARRAY.equals(value)) {
                    asserter = new ArrayIsEmptyAsserter(path);
                }
                else if (path.endsWith(ASSERT_PATH_SIZE)) {
                    path = path.substring(0, path.length() - ASSERT_PATH_SIZE.length());
                    if(value instanceof Number){
                        asserter = new ArraySizeAsserter(path, ((Integer) value).intValue());
                    } else if(value instanceof String){
                        asserter = new ArraySizeAsserter(path, (String)value);
                    } else {
                        throw new RuntimeException(format("Oops! Unsupported value for .SIZE: %s", value));
                    }
                }
                else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_CONTAINS_STRING)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_CONTAINS_STRING.length());
                    asserter = new FieldHasSubStringValueAsserter(path, expected);
                }
                else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_MATCHES_STRING)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_MATCHES_STRING.length());
                    asserter = new FieldMatchesRegexPatternAsserter(path, expected);
                }
                else if (value instanceof String && ((String) value).startsWith(ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE.length());
                    asserter = new FieldHasSubStringIgnoreCaseValueAsserter(path, expected);
                }
                else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasEqualNumberValueAsserter(path, new BigDecimal(expected));
                }
                else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_NOT_EQUAL_TO_NUMBER.length());
                    asserter = new FieldHasInEqualNumberValueAsserter(path, new BigDecimal(expected));
                }
                else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_GREATER_THAN)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_GREATER_THAN.length());
                    asserter = new FieldHasGreaterThanValueAsserter(path, new BigDecimal(expected));
                }
                else if (value instanceof String && (value.toString()).startsWith(ASSERT_VALUE_LESSER_THAN)) {
                    String expected = ((String) value).substring(ASSERT_VALUE_LESSER_THAN.length());
                    asserter = new FieldHasLesserThanValueAsserter(path, new BigDecimal(expected));
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

        }
        else if (jsonNode.getNodeType().equals(JsonNodeType.ARRAY)) {
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
        else if(jsonNode.isValueNode()){
            Object value = convertJsonTypeToJavaType(jsonNode);
            resultMap.put("$", value);

        }
        else {
            throw new RuntimeException(format("Oops! Unsupported JSON Type: %s", jsonNode.getClass().getName()));

        }

        return resultMap;
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
            builder.append((char) ('a' + r.nextInt(26)));
        }
        String randomString = builder.toString();
        return randomString;
    }

    private String createStaticAlphaString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('a' + i));

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

    public String getXmlContent(String xmlFileResource){
        try {
            return SmartUtils.readJsonAsString(xmlFileResource);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Oops! Problem occurred while reading the XML file '" + xmlFileResource
                    + "', details:" + e);
        }
    }

}
