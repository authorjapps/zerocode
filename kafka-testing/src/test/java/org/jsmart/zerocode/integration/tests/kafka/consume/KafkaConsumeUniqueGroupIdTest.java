package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_unique.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeUniqueGroupIdTest {

    @Test
    @Scenario("kafka/consume/test_kafka_consume.json")
    public void testKafkaConsume() throws Exception {
    }

}
