package org.jsmart.zerocode.integration.tests.kafka.consume.negative;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_avro.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeAvroNegativeTest {

    @Test
    @Scenario("kafka/consume/negative/test_kafka_rest_proxy_avro_msg_wrong_value.json")
    public void testKafkaConsume_avroWrongValue() throws Exception {
    }

    @Ignore("Users Requested to ignore this until io.confluent:kafka-avro-serializer:5.1.0 becomes available at maven central." +
            "But to see these tests Passing - Visit repo >> https://github.com/authorjapps/hello-kafka-stream-testing")
    @Test
    @Scenario("kafka/consume/negative/test_load_kafka_direct_invalid_avro_msg.json")
    public void testKafkaWrongData_loadDirectTopic() throws Exception {
    }

}
