package org.jsmart.zerocode.integration.tests.kafka.produce.negative;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceSyncWrongFileNameTest {

    @Test
    @Scenario("kafka/produce/negative/test_kafka_produce_from_worng_filename.json")
    public void testProduceAnd_wrongFileName() throws Exception {
    }

}
