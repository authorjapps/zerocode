package org.jsmart.zerocode.core.engine.executor.javaapi;

import java.util.Map;

public interface CustomAsserter {
    Boolean asserted(Map<String, Object> inputParamMap, Object actualFieldValue);
}
