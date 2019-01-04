package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.send.message.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.jsmart.zerocode.core.kafka.common.CommonConfigs.BOOTSTRAP_SERVERS;
import static org.jsmart.zerocode.core.kafka.error.KafkaMessageConstants.NO_RECORD_FOUND_TO_SEND;

public class KafkaProducerHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerHelper.class);
    private static final Gson gson = new GsonSerDeProvider().get();
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    public static Producer<Long, String> createProducer(String bootStrapServers, String producerPropertyFile) {

        try (InputStream propsIs = Resources.getResource(producerPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put(BOOTSTRAP_SERVERS, bootStrapServers);

            return new KafkaProducer(properties);

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka producer properties" + e);
        }
    }

    public static List<ProducerRecord> validateProduceRecord(Records producerRecords) {
        List<ProducerRecord> recordsToSend = producerRecords.getRecords();
        if (recordsToSend == null || recordsToSend.size() == 0) {
            throw new RuntimeException(NO_RECORD_FOUND_TO_SEND);
        }
        return recordsToSend;
    }

    public static ProducerRecord prepareRecordToSend(String topicName, ProducerRecord recordToSend) {

        return new ProducerRecord(topicName,
                recordToSend.partition(),
                recordToSend.timestamp(),
                recordToSend.key(),
                recordToSend.value());
    }
}
