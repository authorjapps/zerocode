package org.jsmart.zerocode.integration.tests.kafka.protobuf;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_protobuf.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaProtobufTest {

    @Test
    @Scenario("kafka/produce-consume/test_kafka_protobuf.json")
    public void testProduceConsume() throws Exception {
    }

}
