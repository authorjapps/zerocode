package org.jsmart.zerocode.integration.tests.kafka.produce;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_int_key.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceJsonTest {

    @Test
    @Scenario("kafka/produce/test_kafka_produce_json_record.json")
    public void testProduce_json() throws Exception {
    }

}
