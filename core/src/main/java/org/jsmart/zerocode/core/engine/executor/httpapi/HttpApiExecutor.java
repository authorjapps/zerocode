package org.jsmart.zerocode.core.engine.executor.httpapi;

public interface HttpApiExecutor {
    String execute(String urlName, String methodName, String requestJson) throws Exception;
}
