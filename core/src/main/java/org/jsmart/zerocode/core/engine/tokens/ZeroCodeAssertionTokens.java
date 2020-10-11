package org.jsmart.zerocode.core.engine.tokens;

/**
 * This class contains Assertion placeholder tokens.
 * These are used during test assertion which is similar to Hamcrest matchers.
 */
public class ZeroCodeAssertionTokens {
    public static final String ASSERT_VALUE_CONTAINS_STRING = "$CONTAINS.STRING:";
    public static final String ASSERT_VALUE_MATCHES_STRING = "$MATCHES.STRING:";
    public static final String ASSERT_VALUE_EQUAL_TO_NUMBER = "$EQ.";
    public static final String ASSERT_VALUE_NOT_EQUAL_TO_NUMBER = "$NOT.EQ.";
    public static final String ASSERT_VALUE_GREATER_THAN = "$GT.";
    public static final String ASSERT_VALUE_LESSER_THAN = "$LT.";
    public static final String ASSERT_VALUE_IS_NOT_NULL = "$IS.NOTNULL";
    public static final String ASSERT_VALUE_NOT_NULL = "$NOT.NULL";
    public static final String ASSERT_VALUE_IS_NULL = "$IS.NULL";
    public static final String ASSERT_VALUE_NULL = "$NULL";
    public static final String ASSERT_VALUE_CUSTOM_ASSERT = "$CUSTOM.ASSERT:";
    public static final String ASSERT_VALUE_EMPTY_ARRAY = "$[]";
    public static final String ASSERT_PATH_SIZE = ".SIZE";
    public static final String ASSERT_VALUE_CONTAINS_STRING_IGNORE_CASE = "$CONTAINS.STRING.IGNORECASE:";
    public static final String ASSERT_LOCAL_DATETIME_AFTER = "$LOCAL.DATETIME.AFTER:";
    public static final String ASSERT_LOCAL_DATETIME_BEFORE = "$LOCAL.DATETIME.BEFORE:";
    public static final String ASSERT_VALUE_ONE_OF = "$ONE.OF:";
    public static final String ASSERT_VALUE_IS_ONE_OF = "$IS.ONE.OF:";
    public static final String ASSERT_PATH_VALUE_NODE = "$";
    public static final String RAW_BODY = ".rawBody";

}
