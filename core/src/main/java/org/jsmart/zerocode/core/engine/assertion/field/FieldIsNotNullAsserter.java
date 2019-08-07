package org.jsmart.zerocode.core.engine.assertion.field;

import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createMatchingMessage;
import static org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher.createNotMatchingMessage;

import org.jsmart.zerocode.core.engine.assertion.FieldAssertionMatcher;
import org.jsmart.zerocode.core.engine.assertion.JsonAsserter;

public class FieldIsNotNullAsserter implements JsonAsserter {
  private final String path;

  public FieldIsNotNullAsserter(String path) {
    this.path = path;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public FieldAssertionMatcher actualEqualsToExpected(Object result) {
    return result != null
        ? createMatchingMessage()
        : createNotMatchingMessage(path, "NOT NULL", result);
  }
}
