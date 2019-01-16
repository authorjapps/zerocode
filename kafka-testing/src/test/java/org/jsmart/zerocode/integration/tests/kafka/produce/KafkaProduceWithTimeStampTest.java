package org.jsmart.zerocode.integration.tests.kafka.produce;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceWithTimeStampTest {

    @Test
    @JsonTestCase("kafka/produce/test_kafka_produce_with_timestamp.json")
    public void testProduceWith_timestamp() throws Exception {
    }

}
