package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.send.message.ProducerJsonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.common.CommonConfigs.BOOTSTRAP_SERVERS;
import static org.jsmart.zerocode.core.kafka.common.KafkaCommonUtils.resolveValuePlaceHolders;
import static org.jsmart.zerocode.core.kafka.error.KafkaMessageConstants.NO_RECORD_FOUND_TO_SEND;
import static org.jsmart.zerocode.core.utils.TokenUtils.resolveKnownTokens;

public class KafkaProducerHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerHelper.class);
    private static final Gson gson = new GsonSerDeProvider().get();
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    public  static Producer<Long, String> createProducer(String bootStrapServers, String producerPropertyFile) {
        try (InputStream propsIs = Resources.getResource(producerPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put(BOOTSTRAP_SERVERS, bootStrapServers);

            resolveValuePlaceHolders(properties);

            return new KafkaProducer(properties);

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka producer properties - " + e);
        }
    }

    public static void validateProduceRecord(List producerRecords) {

        if (producerRecords == null || producerRecords.size() == 0) {
            throw new RuntimeException(NO_RECORD_FOUND_TO_SEND);
        }
    }

    public static ProducerRecord prepareRecordToSend(String topicName, ProducerRecord recordToSend) {

        return new ProducerRecord(topicName,
                recordToSend.partition(),
                recordToSend.timestamp(),
                recordToSend.key(),
                recordToSend.value());
    }

    public static ProducerRecord prepareJsonRecordToSend(String topicName, ProducerJsonRecord recordToSend) {

        return new ProducerRecord(topicName,
                //recordToSend.partition(),
                //recordToSend.timestamp(),
                recordToSend.getKey(),
                // --------------------------------------------
                // It's a JSON as String. Nothing to worry !
                // Kafka StringSerializer needs in this format.
                // --------------------------------------------
                recordToSend.getValue().toString()
        );
    }


    public static String readRecordType(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.warn("Could not find path '" + jsonPath + "' in the request. returned default type 'RAW'.");
            return RAW;
        }
    }

}
