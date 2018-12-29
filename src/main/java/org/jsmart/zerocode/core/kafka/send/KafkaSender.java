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

        List<ProducerRecord> records = producerRecords.getRecords();
        if (records == null || records.size() == 0) {
            throw new RuntimeException("--------> No record was found or invalid record format was found <--------");
        }
        try {
            for (int i = 0; i < records.size(); i++) {
                // Very basic constructor, use other ones for 'partition' key etc
                ProducerRecord record = new ProducerRecord(topicName, records.get(0).key(), records.get(0).value());

                RecordMetadata metadata = (RecordMetadata) producer.send(record).get();
                LOGGER.info("Record sent to partition- {}, with offset- {}, record- {}  ", metadata.partition(), metadata.offset(), record);

                // logs deliveryDetails, but sends the final one to the caller
                // TODO- combine deliveryDetails into a list n return
                deliveryDetails = gson.toJson(new DeliveryDetails(OK, metadata));
                LOGGER.info("deliveryDetails- {}", deliveryDetails);
            }

        } catch (Exception e) {
            LOGGER.info("Error in sending record. Exception - {} ", e);
            String failedStatus = objectMapper.writeValueAsString(new DeliveryDetails(FAILED, e.getMessage()));
            //sends the final record delivery status only.
            return prettyPrintJson(failedStatus);
        } finally {
            producer.close();
        }

        return prettyPrintJson(deliveryDetails);

    }

}
