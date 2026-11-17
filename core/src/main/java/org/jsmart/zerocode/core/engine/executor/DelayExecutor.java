package org.jsmart.zerocode.core.engine.executor;

public interface DelayExecutor {
    String execute(String url, String operationName, String requestJson);
}
