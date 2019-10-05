package org.jsmart.zerocode.integration.tests.more.customclient;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseKafkaClient;
import org.jsmart.zerocode.core.kafka.client.ZerocodeCustomKafkaClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@UseKafkaClient(ZerocodeCustomKafkaClient.class)
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProduceCustomClientTest {

    @Test
    @Scenario("kafka/produce/test_kafka_produce.json")
    public void testPublish() throws Exception {
    }

}
