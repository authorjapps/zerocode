package org.jsmart.zerocode.integration.tests.more.ksql;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server_avro.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaKsqlTest {

    @Test
    @JsonTestCase("kafka/consume/ksql/test_ksql_query.json")
    public void testKafkaConsume_ksql() throws Exception {
    }

}
