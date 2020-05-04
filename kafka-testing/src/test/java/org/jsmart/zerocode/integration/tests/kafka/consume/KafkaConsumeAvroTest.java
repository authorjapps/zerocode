package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore("Users Requested to ignore this until io.confluent:kafka-avro-serializer:5.1.0 becomes available at maven central." +
        "But to see these tests Passing - Visit repo >> https://github.com/authorjapps/hello-kafka-stream-testing")
@TargetEnv("kafka_servers/kafka_test_server_avro.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeAvroTest {

    @Test
    @Scenario("kafka/consume/test_kafka_consume_avro_msg_json.json")
    public void testKafkaConsume_avroJson() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/test_kafka_consume_avro_msg_raw_int.json")
    public void testKafkaConsume_avroRaw() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/test_kafka_consume_avro_msg_raw_json.json")
    public void testKafkaConsume_avroRawJson() throws Exception {
    }

}
