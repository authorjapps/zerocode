package org.jsmart.zerocode.core.kafka.helper;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KafkaFileRecordHelperTest {

    @Test
    public void test_fileDump_RAW() throws IOException {
        ConsumerRecord consumerRecord1 = new ConsumerRecord("topic.x", 1, 1, "key1", "value1");
        ConsumerRecord consumerRecord2 = new ConsumerRecord("topic.x", 2, 2, "key2", "value2");

        List<ConsumerRecord> records = new ArrayList<>();
        records.add(consumerRecord1);
        records.add(consumerRecord2);

        String relativeFileName = "target/tempX/ttt/demo1.txt";
        //String absoluteFileName = "also works for full path - Tested Ok";
        KafkaFileRecordHelper.dumpRawRecordsIfEnabled(relativeFileName, records);

        long lineCount = Files.lines(Paths.get(relativeFileName)).count();
        assertThat(lineCount, is(2L));
    }

    // Caused by: java.lang.RuntimeException: Create file '//////zerocode/target/temp/demo2.txt'
    // Exceptionjava.nio.file.AccessDeniedException: /zerocode
    @Test(expected = RuntimeException.class)
    public void test_dodgyFile() throws IOException {
        String badAbsoluteFileName = "//////zerocode/target/temp/demo2.txt";
        KafkaFileRecordHelper.dumpRawRecordsIfEnabled(badAbsoluteFileName, new ArrayList<>());
    }
}