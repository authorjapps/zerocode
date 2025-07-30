package org.jsmart.zerocode.integration.tests.kafka.produce;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_double_key.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceRawTest {

    @Test
    @Scenario("kafka/produce/test_kafka_produce_raw.json")
    public void testProduce_raw() throws Exception {
    }

}
