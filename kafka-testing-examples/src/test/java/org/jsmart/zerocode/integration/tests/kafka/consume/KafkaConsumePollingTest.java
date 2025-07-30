package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_polling.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumePollingTest {

    /**
     * When no polling time is explicitly defined in properties
     * file e.g consumer.pollingTime
     * Then intial poll consumer join will default to program
     * defined default of 500ms.
     */
    @Test
    @Scenario("kafka/consume/test_kafka_consume.json")
    public void testKafkaConsume() throws Exception {
    }

}
