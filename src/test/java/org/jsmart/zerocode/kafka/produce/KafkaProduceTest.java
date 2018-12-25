package org.jsmart.zerocode.kafka.produce;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore("Ignored for CI build until docker ci build setup is complete. But can be run manually against a kafka server.")
@TargetEnv("hosts_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceTest {

    @Test
    @JsonTestCase("kafka/produce/test_kafka_publish.json")
    public void testPublish() throws Exception {
    }

}
