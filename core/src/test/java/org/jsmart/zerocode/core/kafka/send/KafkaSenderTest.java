package org.jsmart.zerocode.core.kafka.send;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.kafka.send.message.ProducerRawRecords;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class KafkaSenderTest {
    private final Gson gson = new GsonSerDeProvider().get();


    @Test
    public void testReadLineByLine() throws FileNotFoundException {
        List<String> lines = new ArrayList<>();

        File file = new File(getClass().getClassLoader().getResource("integration_test_files/kafka_pfiles/unit_test_data_raw.json").getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            //while ((line = br.readLine()) != null) {
            for(int i = 0; (line = br.readLine()) != null; i++) {
                System.out.println("line Number : " + i + ": " + line);
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertThat(lines.size(), is(2));
        assertThat(lines.get(0), is("{\"key\":\"1539010017093\",\"value\":\"Hello World 1\"}"));
        assertThat(lines.get(1), is("{\"key\":\"1539010017094\",\"value\":\"Hello World 2\"}"));
    }

    @Test
    public void testReadLineByLine_deser() throws FileNotFoundException {

        ProducerRawRecords producerRawRecords = new ProducerRawRecords(null, null, null, "hello/test_file_raw_recs.txt");

        File file = new File(getClass().getClassLoader().getResource("integration_test_files/kafka_pfiles/unit_test_data_raw.json").getFile());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            for(int i = 0; (line = br.readLine()) != null; i++) {
                System.out.println("line Number : " + i + ": " + line);
                ProducerRecord record = gson.fromJson(line, ProducerRecord.class);
                producerRawRecords.getRecords().add(record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ProducerRecord> records = producerRawRecords.getRecords();
        assertThat(records.size(), is(2));
        assertThat(records.get(0).key(), is("1539010017093"));
        assertThat(records.get(0).value(), is("Hello World 1"));
        assertThat(records.get(1).key(), is("1539010017094"));
        assertThat(records.get(1).value(), is("Hello World 2"));
    }
}