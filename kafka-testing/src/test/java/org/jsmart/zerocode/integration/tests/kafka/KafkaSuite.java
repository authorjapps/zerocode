package org.jsmart.zerocode.integration.tests.kafka;

import org.jsmart.zerocode.integration.tests.kafka.consume.*;
import org.jsmart.zerocode.integration.tests.kafka.consume.file.KafkaConsumeDumpToFileTest;
import org.jsmart.zerocode.integration.tests.more.ksql.KafkaKsqlTest;
import org.jsmart.zerocode.integration.tests.kafka.consume.negative.KafkaConsumeAvroNegativeTest;
import org.jsmart.zerocode.integration.tests.more.customclient.KafkaProduceCustomClientTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.*;
import org.jsmart.zerocode.integration.tests.kafka.produce.file.KafkaProduceAsyncFromFileRawTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.file.KafkaProduceSyncFromFileJsonTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.file.KafkaProduceSyncFromFileRawTest;
import org.jsmart.zerocode.integration.tests.kafka.produce.negative.KafkaProduceSyncWrongFileNameTest;
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
        KafkaConsumeRawTest.class,
        KafkaConsumeJsonTest.class,
        KafkaProduceIntKeyTest.class,
        KafkaConsumeIntKeyTest.class,
        KafkaConsumeAvroTest.class,
        KafkaConsumeAvroNegativeTest.class,
        KafkaConsumeDumpToFileTest.class,
        KafkaProduceAsyncTest.class,
        KafkaProduceAsyncFromFileRawTest.class,
        KafkaProduceSyncFromFileRawTest.class,
        KafkaProduceSyncFromFileJsonTest.class,
        KafkaProduceSyncWrongFileNameTest.class,
        KafkaConsumeSeekOffsetTest.class,
        KafkaKsqlTest.class
})
@RunWith(Suite.class)
public class KafkaSuite {
}
