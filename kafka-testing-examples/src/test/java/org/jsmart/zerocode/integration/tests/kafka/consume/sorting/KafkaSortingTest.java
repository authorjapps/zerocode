package org.jsmart.zerocode.integration.tests.kafka.consume.sorting;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaSortingTest {

    @Test
    @Scenario("kafka/consume/sorting/test_kafka_sort_records_by_json_path.json")
    public void testConsumeSort_byJsonPath(){
    }

}
