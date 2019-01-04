package org.jsmart.zerocode.kafka.consume;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_avro.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeAvroTest {

    @Ignore
    @Test
    @JsonTestCase("kafka/consume/WIP_CI_test_kafka_consume_avro_msg.json")
    public void testKafkaConsume_avro() throws Exception {
    }
}
