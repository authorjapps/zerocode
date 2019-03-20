package org.jsmart.zerocode.core.engine.preprocessor;

import java.util.List;

import static java.util.Arrays.asList;

public class ZeroCodeTokens {
    /*
     * General place holders
     */
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


    public static List<String> getKnownTokens() {
        return asList(
                PREFIX_ASU,
                RANDOM_NUMBER,
                RANDOM_STRING_PREFIX,
                STATIC_ALPHABET,
                LOCALDATE_TODAY,
                LOCALDATETIME_NOW,
                XML_FILE,
                RANDOM_UU_ID,
                RECORD_DUMP
        );
    }
}
