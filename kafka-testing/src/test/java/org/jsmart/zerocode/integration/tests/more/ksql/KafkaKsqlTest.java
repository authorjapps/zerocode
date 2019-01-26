package org.jsmart.zerocode.integration.tests.more.ksql;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_ksql.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaKsqlTest {

    @Test
    @JsonTestCase("kafka/consume/ksql/test_ksql_query.json")
    public void testKafkaConsume_ksql() throws Exception {
    }

    @Ignore ("Hangs indefinitely, Raised issue in Confluent Repo - ksql#2386")
    @Test
    @JsonTestCase("kafka/consume/ksql/WIP_ISSUE_no_comma_test_ksql_print_topic_records.json")
    public void testKafkaConsume_printTopicRawNoComma() throws Exception {
    }

    @Ignore ("Issue still exists with Comma")
    @Test
    @JsonTestCase("kafka/consume/ksql/WIP_ISSUE_test_ksql_print_records.json")
    public void testKafkaConsume_printTopicRaw() throws Exception {
    }

    @Ignore ("Issue still exists")
    @Test
    @JsonTestCase("kafka/consume/ksql/WIP_ISSUE_test_ksql_print_records_json.json")
    public void testKafkaConsume_printTopicJson() throws Exception {
    }

}
