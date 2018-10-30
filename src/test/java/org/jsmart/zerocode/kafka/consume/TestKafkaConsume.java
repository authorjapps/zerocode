package org.jsmart.zerocode.kafka.consume;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hosts_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class TestKafkaConsume {

    @Test
    @JsonTestCase("kafka/consume/test_kafka_subscribe.json")
    public void testKafkaConsume() throws Exception {
    }

    @Test
    @JsonTestCase("kafka/consume/file_dump/test_kafka_consume_raw_record_dump.json")
    public void testKafkaRawRecordDump() throws Exception {
    }

    @Ignore("TODO wip_")
    @Test
    @JsonTestCase("kafka/consume/WIP_test_kafka_consume_with_properties.json")
    public void testKafkaLocalProperties() throws Exception {
    }

    @Ignore("TODO wip_")
    @Test
    @JsonTestCase("kafka/consume/file_dump/WIP_test_kafka_consume_json_dump.json")
    public void testKafkaJsonDump() throws Exception {
    }



}
