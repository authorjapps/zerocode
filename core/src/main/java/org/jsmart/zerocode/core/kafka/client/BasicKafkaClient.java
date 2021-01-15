package org.jsmart.zerocode.core.kafka.client;

import com.google.inject.Inject;
import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.kafka.receive.KafkaReceiver;
import org.jsmart.zerocode.core.kafka.send.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicKafkaClient {
    private Logger LOGGER = LoggerFactory.getLogger(BasicKafkaClient.class);

    @Inject
    private  KafkaSender sender;

    @Inject
    private  KafkaReceiver receiver;


    public BasicKafkaClient() {
    }

    public String execute(String brokers, String topicName, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState) {
        LOGGER.info("brokers:{}, topicName:{}, operation:{}, requestJson:{}", brokers, topicName, operation, requestJson);

        try {
            switch (operation.toLowerCase()) {
                case "send":
                    break;
                case "load":
                    break;
                case "publish":
                    break;
                case "produce":
                    return sender.send(brokers, topicName, requestJson, scenarioExecutionState);
                    
                case "unload":
                    break;
                case "consume":
                    return receiver.receive(brokers, topicName, requestJson);
                    
                case "receive":
                    break;
                case "subscribe":
                    break;

                case "poll":
                    throw new RuntimeException("poll - Not yet Implemented");

                default:
                    throw new RuntimeException("Unsupported. Framework could not assume a default Kafka operation");
            }

        } catch (Throwable exx) {

            LOGGER.error("Exception during operation:{}, topicName:{}, error:{}", operation, topicName, exx.getMessage());

            throw new RuntimeException(exx);
        }

    }
}
