package org.jsmart.zerocode.integration.tests.kafka.produce;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_double_key.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceIntKeyTest {

    @Test
    @Scenario("kafka/produce/test_kafka_produce_int_key.json")
    public void testProduce_intOrDoubleKey() throws Exception {
    }

}
