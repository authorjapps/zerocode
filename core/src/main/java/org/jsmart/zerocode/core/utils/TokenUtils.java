package org.jsmart.zerocode.core.utils;

import org.apache.commons.lang.text.StrSubstitutor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeTokens.*;

public class TokenUtils {

    public static String resolveKnownTokens(String requestJsonOrAnyString) {
        Map<String, Object> paramMap = new HashMap<>();

        final List<String> testCaseTokens = getTestCaseTokens(requestJsonOrAnyString);

        testCaseTokens.forEach(runTimeToken -> {
            populateParamMap(paramMap, runTimeToken);
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);

        return sub.replace(requestJsonOrAnyString);
    }

    public static void populateParamMap(Map<String, Object> paramaMap, String runTimeToken) {
        getKnownTokens().forEach(inStoreToken -> {
                    if (runTimeToken.startsWith(inStoreToken)) {
                        if (runTimeToken.startsWith(RANDOM_NUMBER)) {
                            paramaMap.put(runTimeToken, System.currentTimeMillis() + "");

                        } else if (runTimeToken.startsWith(RANDOM_STRING_PREFIX)) {
                            int length = Integer.parseInt(runTimeToken.substring(RANDOM_STRING_PREFIX.length()));
                            paramaMap.put(runTimeToken, createRandomAlphaString(length));

                        } else if (runTimeToken.startsWith(STATIC_ALPHABET)) {
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

                        } else if (runTimeToken.startsWith(XML_FILE)) {
                            String xmlFileResource = runTimeToken.substring(XML_FILE.length());
                            final String xmlString = getXmlContent(xmlFileResource);
                            // Used escapeJava, do not use escapeXml as it replaces
                            // with GT LT etc ie what exactly you don't want
                            paramaMap.put(runTimeToken, escapeJava(xmlString));

                        } else if (runTimeToken.startsWith(RANDOM_UU_ID)) {
                            paramaMap.put(runTimeToken, randomUUID().toString());
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

    public static String createRandomAlphaString(int length) {
        StringBuilder builder = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('a' + r.nextInt(26)));
        }
        String randomString = builder.toString();
        return randomString;
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
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Oops! Problem occurred while reading the XML file '" + xmlFileResource
                    + "', details:" + e);
        }
    }
}
