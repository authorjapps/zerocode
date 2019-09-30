package org.jsmart.zerocode.kafka;

import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelloiteCustomKafkaClient extends BasicKafkaClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelloiteCustomKafkaClient.class);

    public DelloiteCustomKafkaClient() {
        super();
        LOGGER.info("Running via Delloite custom-Kafka-client...");
    }

    @Override
    public String execute(String brokers, String topicName, String operation, String requestJson) {
        // ---
        // Use your custom send and receive mechanism here
        // Or else
        // code here your custom logic to manipulate brokers/topic/requestJson
        // to prefix/enrich etc.
        // Then delegate to super.execute(...)
        // ---
        return super.execute(brokers, topicName, operation, requestJson);
    }
}

