package org.jsmart.zerocode.core.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringSubstitutor;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.*;

public class TokenUtils {

    private static final Pattern TEST_CASE_TOKEN_PATTERN = Pattern.compile("\\$\\{(.+?)\\}|\\{\\{(.+?)\\}\\}");

    public static String resolveKnownTokens(String requestJsonOrAnyString) {
        Map<String, Object> paramMap = new HashMap<>();

        final List<String> testCaseTokens = getTestCaseTokens(requestJsonOrAnyString);
        testCaseTokens.stream().distinct().forEach(runTimeToken -> {
            populateParamMap(paramMap, runTimeToken);
        });

        return replaceTokens(requestJsonOrAnyString, paramMap);
    }

    public static void populateParamMap(Map<String, Object> paramaMap, String runTimeToken) {
        getKnownTokens().forEach(inStoreToken -> {
                    if (runTimeToken.startsWith(inStoreToken)) {
                        if (runTimeToken.startsWith(RANDOM_NUMBER)) {
                            String[] slices = runTimeToken.split(":");
                            if (slices.length == 2) {
                                if(runTimeToken.startsWith(RANDOM_NUMBER_FIXED)){
                                    paramaMap.put(runTimeToken,  FixedLengthRandomGenerator.getGenerator(Integer.parseInt(slices[1])).toString());
                                }else{
                                    paramaMap.put(runTimeToken, FixedLengthRandomGenerator.getGenerator(Integer.parseInt(slices[1])));
                                }
                            } else {
                                if(runTimeToken.equals(RANDOM_NUMBER_FIXED)){
                                    paramaMap.put(runTimeToken, new RandomNumberGenerator().toString());
                                }else {
                                    paramaMap.put(runTimeToken, new RandomNumberGenerator());
                                }
                            }

                        }
                        else if (runTimeToken.startsWith(GLOBAL_RANDOM_NUMBER)) {
                            String globalRandomNumber = (String) globalTokenCache.get(GLOBAL_RANDOM_NUMBER);
                            if(globalRandomNumber == null){
                                globalRandomNumber = new RandomNumberGenerator().toString();
                                globalTokenCache.put(GLOBAL_RANDOM_NUMBER, globalRandomNumber);
                            }
                            paramaMap.put(runTimeToken, globalRandomNumber);

                        }
                        else if (runTimeToken.startsWith(RANDOM_STRING_ALPHA)) {
                            int length = Integer.parseInt(runTimeToken.substring(RANDOM_STRING_ALPHA.length()));
                            paramaMap.put(runTimeToken, createRandomAlphaString(length));

                        }
                        else if (runTimeToken.startsWith(RANDOM_STRING_ALPHA_NUMERIC)) {
                            int length = Integer.parseInt(runTimeToken.substring(RANDOM_STRING_ALPHA_NUMERIC.length()));
                            paramaMap.put(runTimeToken, createRandomAlphaNumericString(length));

                        }
                        else if (runTimeToken.startsWith(STATIC_ALPHABET)) {
                            int length = Integer.parseInt(runTimeToken.substring(STATIC_ALPHABET.length()));
                            paramaMap.put(runTimeToken, createStaticAlphaString(length));

                        } else if (runTimeToken.startsWith(LOCALDATE_TODAY)) {
                            String formatPattern = runTimeToken.substring(LOCALDATE_TODAY.length());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
                            paramaMap.put(runTimeToken, LocalDate.now().format(formatter));

                        } else if (runTimeToken.startsWith(LOCALDATETIME_NOW)) {
                            String formatPattern = runTimeToken.substring(LOCALDATETIME_NOW.length());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
                            paramaMap.put(runTimeToken, LocalDateTime.now().format(formatter));

                        } else if (runTimeToken.startsWith(SYSTEM_PROPERTY)) {
                            String propertyName = runTimeToken.substring(SYSTEM_PROPERTY.length());
                            paramaMap.put(runTimeToken, System.getProperty(propertyName));

                        } else if (runTimeToken.startsWith(SYSTEM_ENV)) {
                            String propertyName = runTimeToken.substring(SYSTEM_ENV.length());
                            paramaMap.put(runTimeToken, System.getenv(propertyName));

                        } else if (runTimeToken.startsWith(XML_FILE)) {
                            String xmlFileResource = runTimeToken.substring(XML_FILE.length());
                            final String xmlString = getXmlContent(xmlFileResource);
                            // Used escapeJava, do not use escapeXml as it replaces
                            // with GT LT etc ie what exactly you don't want
                            paramaMap.put(runTimeToken, escapeJava(xmlString));
                        } else if (runTimeToken.startsWith(GQL_FILE)) {
                            String gqlFileResource = runTimeToken.substring(GQL_FILE.length());
                            final String gqlString = getXmlContent(gqlFileResource);
                            // Used escapeJava, do not use escapeXml as it replaces
                            // with GT LT etc ie what exactly you don't want
                            paramaMap.put(runTimeToken, escapeJava(gqlString));

                        } else if (runTimeToken.startsWith(SQL_FILE)) {
                            String sqlFileResource = runTimeToken.substring(SQL_FILE.length());
                            final String sqlString = getXmlContent(sqlFileResource);
                            // Used escapeJava, do not use escapeXml as it replaces
                            // with GT LT etc ie what exactly you don't want
                            paramaMap.put(runTimeToken, escapeJava(sqlString));

                        } else if (runTimeToken.startsWith(RANDOM_UU_ID)) {
                            if(runTimeToken.equals(RANDOM_UU_ID_FIXED)){
                                paramaMap.put(runTimeToken, UUID.randomUUID().toString());
                            }else{
                                paramaMap.put(runTimeToken, new UUIDGenerator());
                            }

                        } else if (runTimeToken.startsWith(ABS_PATH)) {
                            String propertyName = runTimeToken.substring(ABS_PATH.length());
                            paramaMap.put(runTimeToken, absolutePathOf(propertyName));

                        } else if (runTimeToken.startsWith(URL_ENCODED)) {
                            String valueToEncode = runTimeToken.substring(URL_ENCODED.length());
                            /*
                             * A JSON path e.g. ${URLENCODED:$.step_name.response.body.id} is only
                             * known after the previous steps ran, hence it is left as it is here and
                             * gets encoded while the JSON paths are resolved against the scenario state.
                             */
                            if (!valueToEncode.startsWith("$.")) {
                                paramaMap.put(runTimeToken, urlEncoded(resolveNestedToken(valueToEncode)));
                            }
                        }
                    }
                }
        );

    }


    /**
     * URL encodes a value, e.g. "#37:29" becomes "%2337%3A29", so that it can be safely
     * used in the url of a subsequent step.
     */
    public static String urlEncoded(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Can not happen as UTF-8 is always supported, still no swallowing of the cause
            throw new RuntimeException("Oops! Problem occurred while URL encoding '" + value + "', details:" + e);
        }
    }

    /**
     * Resolves a known token wrapped inside another token, e.g. the "RANDOM.STRING:5" of
     * "URLENCODED:RANDOM.STRING:5". A plain value is simply returned as it is.
     */
    private static String resolveNestedToken(String nestedToken) {
        Map<String, Object> nestedParamMap = new HashMap<>();
        populateParamMap(nestedParamMap, nestedToken);

        Object resolvedValue = nestedParamMap.get(nestedToken);

        return resolvedValue == null ? nestedToken : resolvedValue.toString();
    }

    /**
     * This method was introduced later,
     * But Framework uses- ZeroCodeJsonTestProcesorImpl#getTestCaseTokens(java.lang.String)
     */
    public static List<String> getTestCaseTokens(String aString) {

        Matcher matcher = TEST_CASE_TOKEN_PATTERN.matcher(aString);

        List<String> keyTokens = new ArrayList<>();

        while (matcher.find()) {
            keyTokens.add(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
        }

        return keyTokens;
    }

    /**
     * Replaces values in both supported placeholder styles. Existing ${...} replacement is
     * deliberately performed first to preserve its long-standing StringSubstitutor behaviour.
     * A double-brace token is replaced only when a corresponding non-null map value exists;
     * unrelated template expressions therefore remain literal.
     */
    public static String replaceTokens(String value, Map<String, ?> paramMap) {
        String resolved = new StringSubstitutor(paramMap).replace(value);
        Matcher matcher = Pattern.compile("\\{\\{(.+?)\\}\\}").matcher(resolved);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            Object replacement = paramMap.get(matcher.group(1));
            String token = replacement != null ? replacement.toString() : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(token));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public static String createRandomAlphaString(int length) {
        return randomAlphabetic(length);
    }

    public static String createRandomAlphaNumericString(int length) {
        return randomAlphanumeric(length);
    }

    public static String createStaticAlphaString(int length) {
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


    public static String getXmlContent(String xmlFileResource) {
        try {
            return SmartUtils.readJsonAsString(xmlFileResource);
        } catch (RuntimeException e) {
            throw new RuntimeException("Oops! Problem occurred while reading the XML file '" + xmlFileResource
                    + "', details:" + e);
        }
    }

    public static String absolutePathOf(String resourceFilePath) {
        URL res = TokenUtils.class.getClassLoader().getResource(resourceFilePath);
        if(res == null){
            throw new RuntimeException("Wrong file name or path found '" + resourceFilePath + "', Please fix it and rerun.");
        }

        File file = null;
        try {
            file = Paths.get(res.toURI()).toFile();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong while fetching abs path of '" + resourceFilePath + "', " +
                    "Please recheck the file/path. Full exception is : " + e);
        }

        return file.getAbsolutePath();
    }
}
