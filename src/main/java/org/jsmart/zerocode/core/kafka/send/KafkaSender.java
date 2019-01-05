package org.jsmart.zerocode.core.kafka.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.delivery.DeliveryDetails;
import org.jsmart.zerocode.core.kafka.send.message.JsonRecord;
import org.jsmart.zerocode.core.kafka.send.message.JsonRecords;
import org.jsmart.zerocode.core.kafka.send.message.RawRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

        RawRecords producerRawRecords = null;
        Object recordType = readRecordType(requestJson, "$.recordType");

        JsonRecords producerJsonRecords = null;

        try {
            if (RAW.equals(recordType)) {
                producerRawRecords = gson.fromJson(requestJson, RawRecords.class);
                validateProduceRecord(producerRawRecords.getRecords());
                for (int i = 0; i < producerRawRecords.getRecords().size(); i++) {
                    deliveryDetails = sendRaw(topicName, producer, producerRawRecords, i);
                }
            }

            if (JSON.equals(recordType)) {
                producerJsonRecords = objectMapper.readValue(requestJson, JsonRecords.class);
                validateProduceRecord(producerJsonRecords.getRecords());
                for (int i = 0; i < producerJsonRecords.getRecords().size(); i++) {
                    deliveryDetails = sendJson(topicName, producer, producerJsonRecords, i);
                }
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

    private String sendRaw(String topicName, Producer<Long, String> producer, RawRecords rawRecords, int i) throws InterruptedException, java.util.concurrent.ExecutionException {
        String deliveryDetails;
        List<ProducerRecord> rawRecordsToSend = rawRecords.getRecords();
        ProducerRecord recordToSend = rawRecordsToSend.get(i);
        ProducerRecord record = prepareRecordToSend(topicName, recordToSend);

        RecordMetadata metadata;
        if (rawRecords.getAsync() != null && rawRecords.getAsync() == true) {
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

    private String sendJson(String topicName, Producer<Long, String> producer, JsonRecords jsonRecords, int i) throws InterruptedException, java.util.concurrent.ExecutionException {
        String deliveryDetails;
        List<JsonRecord> rawRecordsToSend = jsonRecords.getRecords();
        JsonRecord recordToSend = rawRecordsToSend.get(i);
        ProducerRecord record = prepareJsonRecordToSend(topicName, recordToSend);

        RecordMetadata metadata;
        if (jsonRecords.getAsync() != null && jsonRecords.getAsync() == true) {
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

    private Object readRecordType(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.error("Could not find path '" + jsonPath + "' in the request. returned default type 'RAW'.");
            return RAW;
        }
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
