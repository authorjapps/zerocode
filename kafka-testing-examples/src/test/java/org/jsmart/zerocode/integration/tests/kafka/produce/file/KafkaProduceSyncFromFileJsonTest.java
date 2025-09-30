package org.jsmart.zerocode.integration.tests.kafka.produce.file;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceSyncFromFileJsonTest {

    @Test
    @Scenario("kafka/produce/file_produce/test_kafka_produce_sync_from_file_json.json")
    public void testProduceAnd_syncFromFileJson() throws Exception {
    }

    @Test
    @Scenario("kafka/produce/file_produce/test_kafka_produce_sync_from_file_json_with_ref.json")
    public void testProduceAnd_syncFromFileWithVarsJson() throws Exception {
    }

}
