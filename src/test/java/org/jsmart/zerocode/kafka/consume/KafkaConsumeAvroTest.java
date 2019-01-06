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

    @Test
    @JsonTestCase("kafka/consume/test_kafka_consume_avro_msg_raw.json")
    public void testKafkaConsume_avroRaw() throws Exception {
    }

    @Ignore
    @Test
    @JsonTestCase("kafka/consume/WIP_CI_test_kafka_consume_avro_msg_json.json")
    public void testKafkaConsume_avroJson() throws Exception {
    }


}
