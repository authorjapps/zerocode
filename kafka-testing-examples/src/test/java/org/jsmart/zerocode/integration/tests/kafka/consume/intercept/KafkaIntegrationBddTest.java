package org.jsmart.zerocode.integration.tests.kafka.consume.intercept;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/intercept/kafka_brokers.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaIntegrationBddTest {

    @Test
    @Scenario("kafka/consume/e2e_bdd/test_kafka_e2e_integration_msg.yml")
    public void testKafka_e2eBddJSON() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/e2e_bdd/test_kafka_e2e_integration_msg.json")
    public void testKafka_e2eBddYML() throws Exception {
    }
}
