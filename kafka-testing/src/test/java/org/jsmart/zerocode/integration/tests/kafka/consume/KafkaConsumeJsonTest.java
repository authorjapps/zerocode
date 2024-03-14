package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeJsonTest {

    @Test
    @Scenario("kafka/consume/test_kafka_consume_json_msg.json")
    public void testKafkaConsume_json() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/test_kafka_consume_support_of_jsonpath_in_validators.json")
    public void testKafkaProduceConsume_support_of_jsonpath_expression_in_validators_field() throws Exception {
    }
}
