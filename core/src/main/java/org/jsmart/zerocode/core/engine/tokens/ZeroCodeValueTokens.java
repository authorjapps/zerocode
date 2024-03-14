package org.jsmart.zerocode.core.engine.tokens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * This class contains Dynamic Placeholder Value Tokens.
 * These are replaced by their actual value during runtime
 */
public class ZeroCodeValueTokens {
    public static final String JSON_PAYLOAD_FILE = "JSON.FILE:";
    public static final String YAML_PAYLOAD_FILE = "YAML.FILE:";
    public static final String PREFIX_ASU = "ASU";
    public static final String XML_FILE = "XML.FILE:";
    public static final String GQL_FILE = "GQL.FILE:";
    public static final String RANDOM_UU_ID = "RANDOM.UUID";
    public static final String RANDOM_UU_ID_FIXED = "RANDOM.UUID.FIXED";
    public static final String RECORD_DUMP = "RECORD.DUMP:";
    public static final String RANDOM_NUMBER = "RANDOM.NUMBER";
    public static final String RANDOM_NUMBER_FIXED = "RANDOM.NUMBER.FIXED";
    public static final String GLOBAL_RANDOM_NUMBER = "GLOBAL.RANDOM.NUMBER";
    public static final String RANDOM_STRING_ALPHA = "RANDOM.STRING:";
    public static final String RANDOM_STRING_ALPHA_NUMERIC = "RANDOM.ALPHANUMERIC:";
    public static final String STATIC_ALPHABET = "STATIC.ALPHABET:";
    public static final String LOCALDATE_TODAY = "LOCAL.DATE.TODAY:";
    public static final String LOCALDATETIME_NOW = "LOCAL.DATETIME.NOW:";
    public static final String SYSTEM_PROPERTY = "SYSTEM.PROPERTY:";
    public static final String SYSTEM_ENV = "SYSTEM.ENV:";
    public static final String $VALUE = ".$VALUE";
    public static final String ABS_PATH = "ABS.PATH:";
    public static final String JSON_CONTENT = "JSON.CONTENT:";
    public static final String MASKED = "MASKED:";
    public static final String MASKED_STR = "***masked***";


    public static Map<String, Object> globalTokenCache = new HashMap<>();

    public static List<String> getKnownTokens() {
        return asList(
                MASKED,
                PREFIX_ASU,
                RANDOM_NUMBER,
                GLOBAL_RANDOM_NUMBER,
                RANDOM_STRING_ALPHA,
                RANDOM_STRING_ALPHA_NUMERIC,
                STATIC_ALPHABET,
                LOCALDATE_TODAY,
                LOCALDATETIME_NOW,
                SYSTEM_PROPERTY,
                XML_FILE,
                GQL_FILE,
                RANDOM_UU_ID,
                RECORD_DUMP,
                ABS_PATH,
                SYSTEM_ENV
        );
    }
}
