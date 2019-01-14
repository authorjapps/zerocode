package org.jsmart.zerocode.core.kafka;

public interface KafkaService {

	String consume(String serviceName, String methodName, String requestJson);

    String produce(String kafkaServers, String topicName, String requestJson);

    String execute(String kafkaServers, String topicName, String operation, String requestJson);
}
