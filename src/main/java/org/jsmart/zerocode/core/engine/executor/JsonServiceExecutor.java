package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonServiceExecutor {
    String executeJavaService(String serviceName, String methodName, String requestJson) throws JsonProcessingException;

    String executeRESTService(String urlName, String methodName, String requestJson);

    String executeKafkaService(String kafkaServers, String urlName, String methodName, String requestJson);


}
