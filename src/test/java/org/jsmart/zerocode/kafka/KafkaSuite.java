package org.jsmart.zerocode.kafka;

import org.jsmart.zerocode.kafka.consume.*;
import org.jsmart.zerocode.kafka.consume.negative.KafkaConsumeAvroNegativeTest;
import org.jsmart.zerocode.kafka.customclient.KafkaProduceCustomClientTest;
import org.jsmart.zerocode.kafka.produce.*;
import org.jsmart.zerocode.kafka.produce.file.KafkaProduceAsyncFromFileTest;
import org.jsmart.zerocode.kafka.produce.file.KafkaProduceSyncFromFileTest;
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
        KafkaProduceAsyncFromFileTest.class,
        KafkaProduceSyncFromFileTest.class
})
@RunWith(Suite.class)
public class KafkaSuite {
}
