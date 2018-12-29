package org.jsmart.zerocode.kafka;

import org.jsmart.zerocode.kafka.consume.KafkaConsumeIntKeyTest;
import org.jsmart.zerocode.kafka.consume.KafkaConsumeTest;
import org.jsmart.zerocode.kafka.customclient.KafkaProduceCustomClientTest;
import org.jsmart.zerocode.kafka.produce.KafkaProduceIntKeyTest;
import org.jsmart.zerocode.kafka.produce.KafkaProduceTest;
import org.jsmart.zerocode.kafka.produce.KafkaProduceToPartitionTest;
import org.jsmart.zerocode.kafka.produce.KafkaProduceWithTimeStampTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
        KafkaProduceTest.class,
        KafkaConsumeTest.class,
        KafkaProduceCustomClientTest.class,
        KafkaProduceToPartitionTest.class,
        KafkaProduceWithTimeStampTest.class,
        KafkaProduceIntKeyTest.class,
        KafkaConsumeIntKeyTest.class,
        //KafkaPublishFailureTest.class,
})
@RunWith(Suite.class)
public class KafkaSuite {
}
