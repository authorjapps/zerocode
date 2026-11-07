package org.jsmart.zerocode.core.engine.executor;

import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;

public interface ApiServiceExecutor {
    String executeHttpApi(String url, String methodName, String requestJson);

    String executeJavaOperation(String className, String methodName, String requestJson);

    String executeDelay(String url, String operationName, String requestJson);

    String executeKafkaService(String kafkaServers, String kafkaTopic, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState);
}
