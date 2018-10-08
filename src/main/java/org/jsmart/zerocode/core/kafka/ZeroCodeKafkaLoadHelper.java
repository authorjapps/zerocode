package org.jsmart.zerocode.core.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.jsmart.zerocode.core.domain.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.OK;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class ZeroCodeKafkaLoadHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeKafkaLoadHelper.class);

    private static ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private static Gson gson = new GsonSerDeProvider().get();

    public static String load(String kafkaServers, String topicName, String requestJson) throws JsonProcessingException {
        Producer<Long, String> producer = createProducer(kafkaServers);
        String status = objectMapper.writeValueAsString(new DeliveryStatus(OK));

        for (int index = 0; index < 1; index++) {

            // Very basic constructor, use other ones for 'partition' key etc
            final ProducerRecord<Long, String> record = new ProducerRecord<>(topicName, requestJson);

            try {
                RecordMetadata metadata = producer.send(record).get();
                LOGGER.info("Record sent with key " + index
                        + ", to partition " + metadata.partition()
                        + ", with offset " + metadata.offset());


            } catch (Exception e) {
                LOGGER.info("Error in sending record. Exception - {} ", e);
                String failedStatus = objectMapper.writeValueAsString(new DeliveryStatus(FAILED, e.getMessage()));
                return prettyPrintJson(failedStatus);

            }
        }

        return prettyPrintJson(status);
    }

    private static Producer<Long, String> createProducer(String bootStrapServers) {
        try (InputStream propsIs = Resources.getResource("hosts_servers/kafka_producer.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put("bootstrap.servers", bootStrapServers);
            return new KafkaProducer(properties);
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka properties" + e);
        }
    }

}
