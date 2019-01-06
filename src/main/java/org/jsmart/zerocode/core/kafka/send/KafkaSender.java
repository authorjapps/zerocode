package org.jsmart.zerocode.core.kafka.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.jsmart.zerocode.core.kafka.send.message.ProducerJsonRecord;
import org.jsmart.zerocode.core.kafka.send.message.ProducerJsonRecords;
import org.jsmart.zerocode.core.kafka.send.message.ProducerRawRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.jsmart.zerocode.core.domain.ZerocodeConstants.FAILED;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.OK;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.helper.KafkaProducerHelper.*;
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

        ProducerRawRecords producerProducerRawRecords = null;
        String recordType = readRecordType(requestJson, "$.recordType");

        ProducerJsonRecords producerJsonRecords;

        try {
            switch (recordType) {
                case RAW:
                    producerProducerRawRecords = gson.fromJson(requestJson, ProducerRawRecords.class);
                    validateProduceRecord(producerProducerRawRecords.getRecords());
                    for (int i = 0; i < producerProducerRawRecords.getRecords().size(); i++) {
                        deliveryDetails = sendRaw(topicName, producer, producerProducerRawRecords, i);
                    }
                    break;

                case JSON:
                    producerJsonRecords = objectMapper.readValue(requestJson, ProducerJsonRecords.class);
                    validateProduceRecord(producerJsonRecords.getRecords());
                    for (int i = 0; i < producerJsonRecords.getRecords().size(); i++) {
                        deliveryDetails = sendJson(topicName, producer, producerJsonRecords, i);
                    }
                    break;
                default:
                    throw new RuntimeException("Unsupported recordType '" + recordType + "'. Chose RAW or JSON");
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

    private String sendRaw(String topicName, Producer<Long, String> producer, ProducerRawRecords producerRawRecords, int recNum) throws InterruptedException, java.util.concurrent.ExecutionException {
        String deliveryDetails;
        List<ProducerRecord> rawRecordsToSend = producerRawRecords.getRecords();
        ProducerRecord recordToSend = rawRecordsToSend.get(recNum);
        ProducerRecord record = prepareRecordToSend(topicName, recordToSend);

        RecordMetadata metadata;
        if (producerRawRecords.getAsync() != null && producerRawRecords.getAsync() == true) {
            LOGGER.info("Asynchronous Producer sending record - {}", record);
            metadata = (RecordMetadata) producer.send(record, new ProducerAsyncCallback()).get();
        } else {
            LOGGER.info("Producer sending record - {}", record);
            metadata = (RecordMetadata) producer.send(record).get();
        }

        LOGGER.info("Record was sent to partition- {}, with offset- {} ", metadata.partition(), metadata.offset());

        // --------------------------------------------------------------
        // Logs deliveryDetails, which shd be good enough for the caller
        // TODO- combine deliveryDetails into a list n return (if needed)
        // --------------------------------------------------------------
        deliveryDetails = gson.toJson(new DeliveryDetails(OK, metadata));
        LOGGER.info("deliveryDetails- {}", deliveryDetails);
        return deliveryDetails;
    }

    private String sendJson(String topicName, Producer<Long, String> producer, ProducerJsonRecords producerJsonRecords, int i) throws InterruptedException, java.util.concurrent.ExecutionException {
        String deliveryDetails;
        List<ProducerJsonRecord> rawRecordsToSend = producerJsonRecords.getRecords();
        ProducerJsonRecord recordToSend = rawRecordsToSend.get(i);
        ProducerRecord record = prepareJsonRecordToSend(topicName, recordToSend);

        RecordMetadata metadata;
        if (producerJsonRecords.getAsync() != null && producerJsonRecords.getAsync() == true) {
            LOGGER.info("Asynchronous - Producer sending JSON record - {}", record);
            metadata = (RecordMetadata) producer.send(record, new ProducerAsyncCallback()).get();
        } else {
            LOGGER.info("Producer sending JSON record - {}", record);
            metadata = (RecordMetadata) producer.send(record).get();
        }

        LOGGER.info("Record was sent to partition- {}, with offset- {} ", metadata.partition(), metadata.offset());

        // --------------------------------------------------------------
        // Logs deliveryDetails, which shd be good enough for the caller
        // TODO- combine deliveryDetails into a list n return (if needed)
        // --------------------------------------------------------------
        deliveryDetails = gson.toJson(new DeliveryDetails(OK, metadata));
        LOGGER.info("deliveryDetails- {}", deliveryDetails);
        return deliveryDetails;
    }


    class ProducerAsyncCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception ex) {
            if (ex != null) {
                LOGGER.error("Asynchronous Producer failed with exception - {} ", ex);
            } else {
                LOGGER.info("Asynchronous Producer call was successful");
            }
        }
    }
}
