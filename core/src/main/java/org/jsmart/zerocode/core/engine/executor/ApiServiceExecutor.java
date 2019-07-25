package org.jsmart.zerocode.core.engine.executor;

public interface ApiServiceExecutor {
    String executeJavaOperation(String serviceName, String methodName, String requestJson);

    String executeHttpApi(String urlName, String methodName, String requestJson);

    String executeKafkaService(String kafkaServers, String urlName, String methodName, String requestJson);

}
