package org.jsmart.zerocode.kafka.customclient;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseKafkaClient;
import org.jsmart.zerocode.core.kafka.client.ZerocodeCustomKafkaClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore("Ignored for CI build until docker ci build setup is complete. But can be run manually against a kafka server.")
@TargetEnv("hosts_servers/kafka_test_server.properties")
@UseKafkaClient(ZerocodeCustomKafkaClient.class)
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceCustomClientTest {

    @Test
    @JsonTestCase("kafka/produce/test_kafka_publish.json")
    public void testPublish() throws Exception {
    }

}
