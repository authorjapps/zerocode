package org.jsmart.zerocode.kafka;

import org.jsmart.zerocode.kafka.consume.*;
import org.jsmart.zerocode.kafka.consume.file.KafkaConsumeDumpToFileTest;
import org.jsmart.zerocode.kafka.consume.negative.KafkaConsumeAvroNegativeTest;
import org.jsmart.zerocode.kafka.customclient.KafkaProduceCustomClientTest;
import org.jsmart.zerocode.kafka.produce.*;
import org.jsmart.zerocode.kafka.produce.file.KafkaProduceAsyncFromFileRawTest;
import org.jsmart.zerocode.kafka.produce.file.KafkaProduceSyncFromFileJsonTest;
import org.jsmart.zerocode.kafka.produce.file.KafkaProduceSyncFromFileRawTest;
import org.jsmart.zerocode.kafka.produce.negative.KafkaProduceSyncWrongFileNameTest;
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
})
@RunWith(Suite.class)
public class KafkaSuite {
}
