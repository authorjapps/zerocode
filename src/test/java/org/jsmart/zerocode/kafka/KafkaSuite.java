package org.jsmart.zerocode.kafka;

import org.jsmart.zerocode.kafka.consume.KafkaConsumeIntKeyTest;
import org.jsmart.zerocode.kafka.consume.KafkaConsumeJsonNotReallyJsonTest;
import org.jsmart.zerocode.kafka.consume.KafkaConsumeTest;
import org.jsmart.zerocode.kafka.customclient.KafkaProduceCustomClientTest;
import org.jsmart.zerocode.kafka.produce.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
        KafkaProduceTest.class,
        KafkaConsumeTest.class,
        KafkaProduceCustomClientTest.class,
        KafkaProduceToPartitionTest.class,
        KafkaProduceWithTimeStampTest.class,
        KafkaProduceTwoRecordsTest.class,
        KafkaConsumeJsonNotReallyJsonTest.class,
        KafkaProduceIntKeyTest.class,
        KafkaConsumeIntKeyTest.class,
        KafkaProduceAsyncTest.class
        //KafkaPublishFailureTest.class,
})
@RunWith(Suite.class)
public class KafkaSuite {
}


