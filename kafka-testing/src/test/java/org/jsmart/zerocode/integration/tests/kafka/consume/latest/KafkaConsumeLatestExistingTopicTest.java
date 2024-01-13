package org.jsmart.zerocode.integration.tests.kafka.consume.latest;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_latest.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeLatestExistingTopicTest {

    @Test
    @Scenario("kafka/consume/latest/test_offset_to_latest_all_partitions_existing_topic.json")
    public void testKafkaConsume_resetToLatestOffsetExistingTopic() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/latest/test_kafka_produce_consume_only_new_msg_existing_topic.json")
    public void testKafkaProduceConsume() throws Exception {
    }
}
