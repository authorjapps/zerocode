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
import org.jsmart.zerocode.core.kafka.send.message.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.jsmart.zerocode.core.domain.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.OK;
import static org.jsmart.zerocode.core.kafka.error.KafkaMessageConstants.NO_RECORD_FOUND_TO_SEND;
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

        Records producerRecords = gson.fromJson(requestJson, Records.class);

        List<ProducerRecord> recordsToSend = producerRecords.getRecords();
        if (recordsToSend == null || recordsToSend.size() == 0) {
            throw new RuntimeException(NO_RECORD_FOUND_TO_SEND);
        }
        try {
            for (int i = 0; i < recordsToSend.size(); i++) {
                ProducerRecord recordToSend = recordsToSend.get(0);
                ProducerRecord record = prepareRecordToSend(topicName, recordToSend);

                RecordMetadata metadata = (RecordMetadata) producer.send(record).get();
                LOGGER.info("Record sent to partition- {}, with offset- {}, record- {}  ", metadata.partition(), metadata.offset(), record);

                // Logs deliveryDetails, which shd be good enough for the caller
                // TODO- combine deliveryDetails into a list n return (if needed)
                deliveryDetails = gson.toJson(new DeliveryDetails(OK, metadata));
                LOGGER.info("deliveryDetails- {}", deliveryDetails);
            }

        } catch (Exception e) {
            LOGGER.info("Error in sending record. Exception - {} ", e);
            String failedStatus = objectMapper.writeValueAsString(new DeliveryDetails(FAILED, e.getMessage()));

            // Sends the final record delivery status only.
            return prettyPrintJson(failedStatus);
        } finally {
            producer.close();
        }

        return prettyPrintJson(deliveryDetails);

    }

    private ProducerRecord prepareRecordToSend(String topicName, ProducerRecord recordToSend) {

        return new ProducerRecord(topicName,
                recordToSend.partition(),
                recordToSend.timestamp(),
                recordToSend.key(),
                recordToSend.value());
    }

}
