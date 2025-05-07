package org.jsmart.zerocode.integration.tests.kafka.consume.file;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeDumpToFileTest {

    @Test
    @Scenario("kafka/consume/file_dump/test_kafka_consume_record_dump_raw_raw.json")
    public void testKafka_RawRecordDump() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/file_dump/test_kafka_consume_record_dump_raw_json.json")
    public void testKafka_RawDumpOfJsonRecord() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/file_dump/test_kafka_consume_record_dump_json_json.json")
    public void testKafka_JsonDumpOfJsonRecord() throws Exception {
    }

}
