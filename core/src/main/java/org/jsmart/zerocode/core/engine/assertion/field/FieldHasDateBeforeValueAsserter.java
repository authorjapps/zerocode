
package org.jsmart.zerocode.core.engine.assertion.field;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;
import static org.jsmart.zerocode.core.utils.DateTimeUtils.parseLocalDateTime;

public class FieldHasDateBeforeValueAsserter implements JsonAsserter {
    private final String path;
    private final LocalDateTime expected;

    public FieldHasDateBeforeValueAsserter(String path, LocalDateTime expected) {
        this.path = path;
        this.expected = expected;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getExpected() {
        return expected;
    }

    @Override
    public FieldAssertionMatcher actualEqualsToExpected(Object result) {
        boolean areEqual;

        if (result == null && expected == null) {
            areEqual = true;
        } else if (result == null) {
            areEqual = false;
        } else {
            LocalDateTime resultDT = null;
            try {
                resultDT = parseLocalDateTime((String) result);
                areEqual = resultDT.isBefore(expected);
            } catch (DateTimeParseException ex) {
                areEqual = false;
            }
        }

        return areEqual ? aMatchingMessage()
                : aNotMatchingMessage(path, "Date Before:" + expected, result);
    }
}
