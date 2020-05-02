package org.jsmart.zerocode.integration.tests.kafka.produce.file;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceAsyncFromFileRawTest {

    @Test
    @Scenario("kafka/produce/file_produce/test_kafka_produce_async_from_file.json")
    public void testProduceAnd_asyncFromFile() throws Exception {
    }

}
