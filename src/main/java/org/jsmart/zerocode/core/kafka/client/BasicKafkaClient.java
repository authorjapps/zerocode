package org.jsmart.zerocode.core.kafka.client;

import org.jsmart.zerocode.core.kafka.ZeroCodeKafkaLoadHelper;
import org.jsmart.zerocode.core.kafka.ZeroCodeKafkaUnloadHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicKafkaClient {
    Logger LOGGER = LoggerFactory.getLogger(BasicKafkaClient.class);


    public BasicKafkaClient() {
    }

    public String execute(String brokers, String topicName, String operation, String requestJson) {
        LOGGER.info("Executing via <<BasicKafkaClient>> brokers:{}, topicName:{}, operation:{}, requestJson:{}",
                brokers, topicName, operation, requestJson);

        try {
            switch (operation) {
                case "publish":
                case "load":
                case "produce":
                case "send":
                    return ZeroCodeKafkaLoadHelper.load(brokers, topicName, requestJson);
                    //return kafkaService.produce(kafkaServers, topicName, requestJson);

                case "subscribe":
                case "unload":
                case "consume":
                case "receive":
                    return ZeroCodeKafkaUnloadHelper.unload(brokers, topicName, requestJson);

                case "poll":
                    return ZeroCodeKafkaLoadHelper.load(brokers, topicName, requestJson);

                default:
                    throw new RuntimeException("Unsupported Kafka operation");
            }

        } catch (Throwable severeExcep) {

            LOGGER.error("Exception during Kafka operation, " +
                            "\n topicName:{}, " +
                            "\n operation:{}, " +
                            "\n error: {}",
                    topicName,
                    operation,
                    severeExcep);

            throw new RuntimeException(severeExcep);

        }

    }
}
