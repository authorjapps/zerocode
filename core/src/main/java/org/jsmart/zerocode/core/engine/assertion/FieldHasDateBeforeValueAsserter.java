package org.jsmart.zerocode.core.engine.assertion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createNotMatchingMessage;

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
                areEqual = resultDT.isBefore(expected);
            } catch (DateTimeParseException ex) {
                areEqual = false;
            }
        }

        return areEqual ? createMatchingMessage()
                : createNotMatchingMessage(path, "Date Before:" + expected, result);
    }
}
