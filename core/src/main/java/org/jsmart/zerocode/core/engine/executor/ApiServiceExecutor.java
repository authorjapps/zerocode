package org.jsmart.zerocode.core.engine.executor;

import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;

public interface ApiServiceExecutor {
    /**
     *
     * @param url A relative path to a http or https url
     * @param methodName An HTTP method e.g. GET, PUT, POST, DELETE, HEAD etc supported by Apache HttpClient
     * @param requestJson A body payload with http headers needed to executeWithParams the HTTP api
     * @return String The response with http headers and body payload in JSON
     */
    String executeHttpApi(String url, String methodName, String requestJson);

    /**
     *
     * @param className A fully qualified java class name
     * @param methodName An accessible public method to executeWithParams
     * @param requestJson A json with fields matching the method parameters
     * @return String The result of the method execution in JSON
     */
    String executeJavaOperation(String className, String methodName, String requestJson);

    /**
     *
     * @param kafkaServers Kafka brokers aka servers
     * @param kafkaTopic Kafka topic(s) residing on the brokers
     * @param methodName A produce or consume or poll operation
     * @param requestJson RAW or JSON records for producing, config settings for consuming
     * @param scenarioExecutionState The state of the scenario execution
     * @return String The broker acknowledgement in JSON
     */
    String executeKafkaService(String kafkaServers, String kafkaTopic, String methodName, String requestJson, ScenarioExecutionState scenarioExecutionState);

}
