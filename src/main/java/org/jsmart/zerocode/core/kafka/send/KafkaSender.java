package org.jsmart.zerocode.core.kafka.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jsmart.zerocode.core.domain.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.OK;
import static org.jsmart.zerocode.core.kafka.helper.KafkaHelper.createProducer;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

@Singleton
public class KafkaSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSender.class);

    @Inject(optional = true)
    @Named("kafka.producer.properties")
    private String producerPropertyFile;


    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private final Gson gson = new GsonSerDeProvider().get();


    public String send(String brokers, String topicName, String requestJson) throws JsonProcessingException {
        Producer<Long, String> producer = createProducer(brokers, producerPropertyFile);
        String deliveryDetails = null;

        // Very basic constructor, use other ones for 'partition' key etc
        final ProducerRecord<Long, String> record = new ProducerRecord<>(topicName, requestJson);

        try {
            RecordMetadata metadata = producer.send(record).get();
            LOGGER.info("Record sent to partition " + metadata.partition()
                    + ", with offset " + metadata.offset());

            deliveryDetails = gson.toJson(new DeliveryDetails(OK, metadata));

        } catch (Exception e) {
            LOGGER.info("Error in sending record. Exception - {} ", e);
            String failedStatus = objectMapper.writeValueAsString(new DeliveryDetails(FAILED, e.getMessage()));

            return prettyPrintJson(failedStatus);
        } finally {
            producer.close();
        }

        return prettyPrintJson(deliveryDetails);

    }

}
