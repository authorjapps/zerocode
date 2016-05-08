package org.jsmart.smarttester.core.runner;

public class JsonAssertionFailureResult {
    private final String path;
    private final Object expected;
    private final Object actual;

    public JsonAssertionFailureResult(String path, Object expected, Object actual) {
        this.path = path;
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String toString() {
        return String.format("Actual value %s on path '%s' did not match the expected value '%s'", actual, path, expected);
    }
}
