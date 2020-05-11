package org.jsmart.zerocode.integration.tests.kafka.consume;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeSeekOffsetTest {

    // Locally paases per run i.e. once . Fails if runs again as offset increases.
    // docker-compose down to reset the offset to 0. Then it can pass again.
    // "seek": "demo-c3,0,1", "demo-c3,0,3", "demo-c3,0,5" manually to see it passing
    // Note- it will always pass in CI, due to fresh container spins up.
    @Ignore("Unignore this when you find out how to run this repeatedly. Otherwise release to mvn central will fail")
    @Test
    @Scenario("kafka/consume/test_kafka_consume_seek_offset.json")
    public void testKafkaConsume_seekOffset() throws Exception {
    }

}
