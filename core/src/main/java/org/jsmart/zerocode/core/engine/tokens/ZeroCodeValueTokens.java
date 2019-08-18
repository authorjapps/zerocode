package org.jsmart.zerocode.core.engine.tokens;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * This class contains Dynamic Placeholder Value Tokens.
 * These are replaced by their actual value in runtime
 */
public class ZeroCodeValueTokens {
    public static final String JSON_PAYLOAD_FILE = "JSON.FILE:";
    public static final String PREFIX_ASU = "ASU";
    public static final String XML_FILE = "XML.FILE:";
    public static final String RANDOM_UU_ID = "RANDOM.UUID";
    public static final String RECORD_DUMP = "RECORD.DUMP:";
    public static final String RANDOM_NUMBER = "RANDOM.NUMBER";
    public static final String RANDOM_STRING_PREFIX = "RANDOM.STRING:";
    public static final String STATIC_ALPHABET = "STATIC.ALPHABET:";
    public static final String LOCALDATE_TODAY = "LOCAL.DATE.TODAY:";
    public static final String LOCALDATETIME_NOW = "LOCAL.DATETIME.NOW:";
    public static final String SYSTEM_PROPERTY = "SYSTEM.PROPERTY:";
    public static final String SYSTEM_ENV = "SYSTEM.ENV:";
    public static final String $VALUE = ".$VALUE";

    public static List<String> getKnownTokens() {
        return asList(
                PREFIX_ASU,
                RANDOM_NUMBER,
                RANDOM_STRING_PREFIX,
                STATIC_ALPHABET,
                LOCALDATE_TODAY,
                LOCALDATETIME_NOW,
                SYSTEM_PROPERTY,
                XML_FILE,
                RANDOM_UU_ID,
                RECORD_DUMP,
                SYSTEM_ENV
        );
    }
}
