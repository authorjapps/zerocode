package org.jsmart.zerocode.integration.tests.kafka.consume.stringkey;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("kafka_servers/kafka_test_server.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class KafkaConsumeJsonAlphaStringKeyTest {

    @Test
    @Scenario("kafka/consume/stringkey/test_kafka_produce_consume_json_string_key.json")
    public void testKafkaConsumeKey_AlphaNumericKey() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/stringkey/test_kafka_produce_consume_json_numberasstring_key.json")
    public void testKafkaConsumeKey_NumberAsStringKey() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/stringkey/test_kafka_produce_consume_jsonobject_as_string_key.json")
    public void testKafkaConsumeKey_JsonObjectAsStringKey() throws Exception {
    }

    @Test
    @Scenario("kafka/consume/stringkey/test_kafka_produce_consume_string_with_double_quotes_key.json")
    public void testKafkaConsumeKey_DoubleQuotedStringKey() throws Exception {
    }

    // JSON Object Key as JSON Object (the below way) is not supported.
    // Instead, use the JSON Object as a String format :
    // e.g. if you want to produce a JSON "key":{"id": "value"}, then produce the "key":"{\"id\": \"value\"}"
    @Ignore
    @Test
    @Scenario("kafka/consume/stringkey/test_kafka_produce_consume_jsonobject_as_objectblock_key.json")
    public void testKafkaConsumeKey_JsonObjectAsObjectlockKey() throws Exception {
    }

}
