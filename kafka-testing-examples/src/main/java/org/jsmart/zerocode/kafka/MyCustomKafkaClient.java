package org.jsmart.zerocode.kafka;

import org.jsmart.zerocode.core.engine.preprocessor.ScenarioExecutionState;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MyCustomKafkaClient extends BasicKafkaClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomKafkaClient.class);
    private boolean customCodeExecuted;

    public MyCustomKafkaClient() {
        super();
        LOGGER.debug("Running via Deloitte custom-Kafka-client...");
    }

    @Override
    public String execute(String brokers, String topicName, String operation, String requestJson, ScenarioExecutionState scenarioExecutionState) {
        customCodeExecuted = true;
        // ---
        // Use your custom send and receive mechanism here
        // Or else,
        // Code here your custom logic to manipulate brokers/topic/requestJson
        // to prefix/enrich the messages etc.
        // Then delegate to super.execute(...)
        // ---

        // Just a sanity check if flow has hit this point or not.
        assertThat(customCodeExecuted, is(true));

        return super.execute(brokers, topicName, operation, requestJson, scenarioExecutionState);
    }
}

