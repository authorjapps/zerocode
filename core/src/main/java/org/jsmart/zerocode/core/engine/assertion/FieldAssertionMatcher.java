package org.jsmart.zerocode.core.engine.assertion;

public class FieldAssertionMatcher {

    private final Object expected;
    private final Object actual;
    private final String jsonPath;

    FieldAssertionMatcher(String path, Object expected, Object actual) {
        this.jsonPath = path;
        this.expected = expected;
        this.actual = actual;

    }

    FieldAssertionMatcher(String path) {
        this(path, null, null);
    }

    public boolean matches() {
        // -------------------------------------------------------------------
        // For test SUCCESS,there is no need of sending the jsonPath.
        // Framework is only concerned about failures i.e. un-matching values.
        // -------------------------------------------------------------------
        return null == getJsonPath();
    }


    public static FieldAssertionMatcher createMatchingMessage() {
        // -------------------------------------------------------------------
        // Because the values were matching, path is not relevant in this case
        // -------------------------------------------------------------------
        return new FieldAssertionMatcher(null);
    }

    public static FieldAssertionMatcher createNotMatchingMessage(String path, Object expected, Object actual) {
        return new FieldAssertionMatcher(path, expected, actual);
    }

    public Object getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    @Override
    public String toString() {
        return matches() ?
                "Actual field value matched the expected field value" :
                String.format("Assertion jsonPath '%s' with actual value '%s' did not match the expected value '%s'",
                        jsonPath, actual, expected);
    }
}
