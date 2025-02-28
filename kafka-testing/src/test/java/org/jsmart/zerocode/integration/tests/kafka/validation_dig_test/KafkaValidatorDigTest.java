package org.jsmart.zerocode.integration.tests.kafka.validation_dig_test;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaValidatorDigTest {

    @Test
    @Scenario("kafka/consume/validator_dig_test/test_kafka_produce_consume_validator_dig_replace_tokens.json")
    public void test_validator_dig() {
    }

}
