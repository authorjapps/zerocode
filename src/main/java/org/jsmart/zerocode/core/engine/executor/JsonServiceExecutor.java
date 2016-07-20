package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonServiceExecutor {
    public String executeJavaService(String serviceName, String methodName, String requestJson) throws JsonProcessingException;

    public String executeRESTService(String urlName, String methodName, String requestJson);
}
