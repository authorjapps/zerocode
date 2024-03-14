package org.jsmart.zerocode.core.utils;

import org.apache.commons.lang.text.StrSubstitutor;

import java.io.File;
import java.net.URL;
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

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.*;

public class TokenUtils {

    public static String resolveKnownTokens(String requestJsonOrAnyString) {
        Map<String, Object> paramMap = new HashMap<>();

        final List<String> testCaseTokens = getTestCaseTokens(requestJsonOrAnyString);
        testCaseTokens.stream().distinct().forEach(runTimeToken -> {
            populateParamMap(paramMap, runTimeToken);
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(requestJsonOrAnyString);
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

                        } else if (runTimeToken.startsWith(RANDOM_UU_ID)) {
                            if(runTimeToken.equals(RANDOM_UU_ID_FIXED)){
                                paramaMap.put(runTimeToken, UUID.randomUUID().toString());
                            }else{
                                paramaMap.put(runTimeToken, new UUIDGenerator());
                            }

                        } else if (runTimeToken.startsWith(ABS_PATH)) {
                            String propertyName = runTimeToken.substring(ABS_PATH.length());
                            paramaMap.put(runTimeToken, absolutePathOf(propertyName));
                        }
                    }
                }
        );

    }


    /**
     * This method was introduced later,
     * But Framework uses- ZeroCodeJsonTestProcesorImpl#getTestCaseTokens(java.lang.String)
     */
    public static List<String> getTestCaseTokens(String aString) {

        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(aString);

        List<String> keyTokens = new ArrayList<>();

        while (matcher.find()) {
            keyTokens.add(matcher.group(1));
        }

        return keyTokens;
    }

    public static String getMasksReplaced(String aString) {
        String regex = "\\$\\{MASKED:([^\\}]*)\\}";
        Matcher maskMatcher = Pattern.compile(regex).matcher(aString);
        while(maskMatcher.find()) {
            String foundMatch = maskMatcher.group(0);
            aString = aString.replace(foundMatch, MASKED_STR);
        }

        return aString;
    }

    public static String getMasksRemoved(String aString) {
        String regex = "\\$\\{MASKED:([^\\}]*)\\}";
        Matcher maskMatcher = Pattern.compile(regex).matcher(aString);
        while(maskMatcher.find()) {
            String foundFullMatch = maskMatcher.group(0);
            String innerContent = maskMatcher.group(1);
            aString = aString.replace(foundFullMatch, innerContent);
        }

        return aString;
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
