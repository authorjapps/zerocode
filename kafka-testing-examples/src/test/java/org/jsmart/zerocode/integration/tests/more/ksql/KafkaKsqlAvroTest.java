package org.jsmart.zerocode.integration.tests.more.ksql;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_avro.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaKsqlAvroTest {

    @Ignore ("Issue still exists")
    @Test
    @Scenario("kafka/consume/ksql/WIP_ISSUE_test_ksql_print_records.json")
    public void testKafkaConsume_printTopicRaw() throws Exception {
    }

    @Ignore("Issue still exists")
    @Test
    @Scenario("kafka/consume/ksql/WIP_ISSUE_test_ksql_print_records_json.json")
    public void testKafkaConsume_printTopicJson() throws Exception {
    }

}
