package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_avro.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceConsumeAvroTest {

    @Test
    @Scenario("kafka/produce-consume/test_kafka_produce_consume_avro_records.json")
    public void testKafkaProduceConsume_avro_With_and_Without_Key() throws Exception {
    }
}
