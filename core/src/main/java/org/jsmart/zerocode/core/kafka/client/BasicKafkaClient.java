package org.jsmart.zerocode.core.kafka.client;

import com.google.inject.Inject;
import org.jsmart.zerocode.core.kafka.receive.KafkaReceiver;
import org.jsmart.zerocode.core.kafka.send.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BasicKafkaClient {
    private Logger LOGGER = LoggerFactory.getLogger(BasicKafkaClient.class);

    @Inject
    private  KafkaSender sender;

    @Inject
    private  KafkaReceiver receiver;

    public BasicKafkaClient() {
    }

    public String execute(String brokers, List<String> topicNames, String operation, String requestJson) {
        LOGGER.info("brokers:{}, topicNames:{}, operation:{}, requestJson:{}", brokers, topicNames.toString(), operation, requestJson);

        try {
            switch (operation) {
                case "send":
                case "load":
                case "publish":
                case "produce":
                    //@todo rewrite sender to send in multiple topics
                    return sender.send(brokers, topicNames.get(0), requestJson);

                case "unload":
                case "consume":
                case "receive":
                case "subscribe":
                    return receiver.receive(brokers, topicNames, requestJson);

                case "poll":
                    throw new RuntimeException("poll - Not yet Implemented");

                default:
                    throw new RuntimeException("Unsupported. Framework could not assume a default Kafka operation");
            }

        } catch (Throwable exx) {

            LOGGER.error("Exception during operation:{}, topicName:{}, error:{}", operation, topicNames.get(0), exx.getMessage());

            throw new RuntimeException(exx);
        }
    }
}
