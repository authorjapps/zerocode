package org.jsmart.zerocode.integration.tests.kafka.produce;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceTest {

    @Test
    @Scenario("kafka/produce/test_kafka_produce.json")
    public void testProduce() throws Exception {
    }

    @Test
    @Scenario("kafka/produce/test_kafka_produce_ack_metadata.json")
    public void testProduceAnd_ack() throws Exception {
    }

}
