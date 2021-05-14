package org.jsmart.zerocode.integration.tests.kafka;

import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeAvroTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeIntKeyTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeJsonTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeRawTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeSeekOffsetTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaConsumeXmlTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.KafkaProduceConsumeAvroTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.file.KafkaConsumeDumpToFileTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.negative.KafkaConsumeAvroNegativeTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceAsyncTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceIntKeyTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceJsonTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceRawTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceRawWithHeadersTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceToPartitionTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceTwoRecordsTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceWithHeadersTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.KafkaProduceWithTimeStampTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.file.KafkaProduceAsyncFromFileRawTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.file.KafkaProduceSyncFromFileJsonTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.file.KafkaProduceSyncFromFileRawTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.negative.KafkaProduceSyncWrongFileNameTest;
import org.jsmart.zerocode.integration.tests.kafka.protobuf.KafkaProtobufTest;
import org.jsmart.zerocode.integration.tests.more.customclient.KafkaProduceCustomClientTest;
import org.jsmart.zerocode.integration.tests.more.ksql.KafkaKsqlTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
        KafkaProduceTest.class,
        KafkaConsumeTest.class,
        KafkaProduceCustomClientTest.class,
        KafkaProduceToPartitionTest.class,
        KafkaProduceWithTimeStampTest.class,
        KafkaProduceTwoRecordsTest.class,
        KafkaProduceRawTest.class,
        KafkaProduceJsonTest.class,
        KafkaProduceRawWithHeadersTest.class,
        KafkaProduceWithHeadersTest.class,
        KafkaConsumeRawTest.class,
        KafkaConsumeXmlTest.class,
        KafkaConsumeJsonTest.class,
        KafkaProduceIntKeyTest.class,
        KafkaConsumeIntKeyTest.class,
        KafkaConsumeAvroTest.class,
        KafkaConsumeAvroNegativeTest.class,
        KafkaProduceConsumeAvroTest.class,
        KafkaConsumeDumpToFileTest.class,
        KafkaProduceAsyncTest.class,
        KafkaProduceAsyncFromFileRawTest.class,
        KafkaProduceSyncFromFileRawTest.class,
        KafkaProduceSyncFromFileJsonTest.class,
        KafkaProduceSyncWrongFileNameTest.class,
        KafkaConsumeSeekOffsetTest.class,
        KafkaKsqlTest.class,
        KafkaProtobufTest.class
})
@RunWith(Suite.class)
public class KafkaSuite {
}
