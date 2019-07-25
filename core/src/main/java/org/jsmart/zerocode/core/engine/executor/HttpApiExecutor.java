package org.jsmart.zerocode.core.engine.executor;

public interface HttpApiExecutor {
    String execute(String urlName, String methodName, String requestJson) throws Exception;
}
