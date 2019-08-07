
package org.jsmart.zerocode.core.engine.assertion.field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;
import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.aNotMatchingMessage;

public class FieldHasDateAfterValueAsserter implements JsonAsserter {
    private final String path;
    private final LocalDateTime expected;

    public FieldHasDateAfterValueAsserter(String path, LocalDateTime expected) {
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
                resultDT = LocalDateTime.parse((String) result,
                        DateTimeFormatter.ISO_DATE_TIME);
                areEqual = resultDT.isAfter(expected);
            } catch (DateTimeParseException ex) {
                areEqual = false;
            }
        }

        return areEqual ? aMatchingMessage() : aNotMatchingMessage(path, "Date After:" + expected, result);
    }
}
