package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeTest {

    @Test
    @Scenario("kafka/consume/test_kafka_consume.json")
    public void testKafkaConsume() throws Exception {
    }

    @Ignore("TODO wip_")
    @Test
    @Scenario("kafka/consume/WIP_test_kafka_consume_with_properties.json")
    public void testKafkaLocalProperties() throws Exception {
    }

    @Ignore("TODO wip_")
    @Test
    @Scenario("kafka/consume/file_dump/WIP_test_kafka_consume_json_dump.json")
    public void testKafkaJsonDump() throws Exception {
    }



}
