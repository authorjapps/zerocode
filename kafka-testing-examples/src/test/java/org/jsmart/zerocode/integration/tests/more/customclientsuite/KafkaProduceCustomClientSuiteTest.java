package org.jsmart.zerocode.integration.tests.more.customclientsuite;

import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.domain.UseKafkaClient;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.jsmart.zerocode.kafka.MyCustomKafkaClient;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@TestPackageRoot("kafka/more")
@UseKafkaClient(MyCustomKafkaClient.class)
@RunWith(ZeroCodePackageRunner.class)
public class KafkaProduceCustomClientSuiteTest {
    // no code goes here
}
