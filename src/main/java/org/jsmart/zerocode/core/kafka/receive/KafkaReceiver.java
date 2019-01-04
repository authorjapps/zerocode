package org.jsmart.zerocode.core.kafka.receive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.ConsumedRecords;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.JsonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.time.Duration.ofMillis;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.helper.KafkaFileRecordHelper.handleRecordsDump;
import static org.jsmart.zerocode.core.kafka.helper.KafkaHelper.*;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

@Singleton
public class KafkaReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReceiver.class);

    private static Gson gson = new GsonSerDeProvider().get();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Inject(optional = true)
    @Named("kafka.consumer.properties")
    private String consumerPropertyFile;

    @Inject
    private ConsumerCommonConfigs consumerCommonConfigs;

    public String receive(String kafkaServers, String topicName, String requestJsonWithConfig) throws IOException {

        ConsumerLocalConfigs consumerLocalConfigs = readConsumerLocalTestProperties(requestJsonWithConfig);

        ConsumerLocalConfigs effectiveLocal = deriveEffectiveConfigs(consumerLocalConfigs, consumerCommonConfigs);

        LOGGER.info("\n### Consumer Effective configs:{}\n", effectiveLocal);

        Consumer consumer = createConsumer(kafkaServers, consumerPropertyFile, topicName);

        final ArrayList<ConsumerRecord> rawRecords = new ArrayList<>();
        final List<JsonRecord> jsonRecords = new ArrayList<>();

        int noOfTimeOuts = 0;

        while (true) {
            LOGGER.info("polling records  - noOfTimeOuts reached : " + noOfTimeOuts);

            final ConsumerRecords records = consumer.poll(ofMillis(getPollTime(effectiveLocal)));

            if (records.count() == 0) {
                noOfTimeOuts++;
                if (noOfTimeOuts > getMaxTimeOuts(effectiveLocal)) {
                    break;
                } else {
                    continue;
                }
            } else {
                LOGGER.info("Got {} records after {} timeouts\n", records.count(), noOfTimeOuts);
                // -----------------------------------
                // reset after it fetched some records
                // -----------------------------------
                noOfTimeOuts = 0;
            }

            if (records != null) {
                Iterator recordIterator = records.iterator();

                LOGGER.info("Consumer Chosen RecordType: " + effectiveLocal.getRecordType());

                // -----------
                // Handle RAW
                // -----------
                if(RAW.equals(effectiveLocal.getRecordType())){
                    while (recordIterator.hasNext()) {
                        ConsumerRecord thisRecord = (ConsumerRecord)recordIterator.next();
                        LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                                thisRecord.key(), thisRecord.value(), thisRecord.partition(), thisRecord.offset());
                        rawRecords.add(thisRecord);
                    }
                }

                // -----------
                // Handle JSON
                // -----------
                if(JSON.equals(effectiveLocal.getRecordType())){
                    while (recordIterator.hasNext()) {
                        ConsumerRecord thisRecord = (ConsumerRecord)recordIterator.next();
                        Boolean showRecordMetaData = true;
                        if(showRecordMetaData){
                            LOGGER.info("Consumed - JSON record >> " + thisRecord);
                            rawRecords.add(thisRecord);
                        }

                        Object key = thisRecord.key();
                        Object value = thisRecord.value();
                        LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                                key, thisRecord.value(), thisRecord.partition(), thisRecord.offset());

                        JsonNode valueNode = objectMapper.readTree(value.toString());

                        JsonRecord jsonRecord = new JsonRecord(thisRecord.key(), null, valueNode);

                        jsonRecords.add(jsonRecord);
                    }
                }


            }

            handleCommitSyncAsync(consumer, effectiveLocal);
        }

        consumer.close();

        handleRecordsDump(effectiveLocal, rawRecords);

        return prepareResult(effectiveLocal, jsonRecords, rawRecords);

    }

    private String prepareResult(ConsumerLocalConfigs consumeLocalTestProps,
                                 List<JsonRecord> jsonRecords,
                                 ArrayList<ConsumerRecord> rawRecords) throws JsonProcessingException {

        if (consumeLocalTestProps != null && consumeLocalTestProps.getShowConsumedRecords() == false) {
            return prettyPrintJson(gson.toJson(new ConsumedRecords(jsonRecords.size() == 0 ? rawRecords.size() : 0)));

        } else if (consumeLocalTestProps != null && "RAW".equals(consumeLocalTestProps.getRecordType())) {
            return prettyPrintJson(gson.toJson(new ConsumedRecords(rawRecords)));

        } else if (consumeLocalTestProps != null && "JSON".equals(consumeLocalTestProps.getRecordType())) {
            return prettyPrintJson(objectMapper.writeValueAsString(new ConsumedRecords(jsonRecords)));

        } else {
            // -----------------------------------
            // type "ALL" : Show both raw and JSON
            // -----------------------------------
            return prettyPrintJson(gson.toJson(new ConsumedRecords(jsonRecords, rawRecords, null)));

        }
    }

    private void handleCommitSyncAsync(Consumer<Long, String> consumer, ConsumerLocalConfigs consumeLocalTestProps) {
        if(consumeLocalTestProps == null){
            LOGGER.warn("[No local test configs]-Kafka client neither did `commitAsync()` nor `commitSync()`");
            return;
        }

        Boolean effectiveCommitSync;
        Boolean effectiveCommitAsync;

        Boolean localCommitSync = consumeLocalTestProps.getCommitSync();
        Boolean localCommitAsync = consumeLocalTestProps.getCommitAsync();

        if (localCommitSync == null && localCommitAsync == null) {
            effectiveCommitSync = consumerCommonConfigs.getCommitSync();
            effectiveCommitAsync = consumerCommonConfigs.getCommitAsync();

        } else {
            effectiveCommitSync = localCommitSync;
            effectiveCommitAsync = localCommitAsync;
        }

        if (effectiveCommitSync != null && effectiveCommitSync == true) {
            consumer.commitSync();

        } else if (effectiveCommitAsync != null && effectiveCommitAsync == true) {
            consumer.commitAsync();

        } else {
            LOGGER.warn("Kafka client neither configured for `commitAsync()` nor `commitSync()`");
        }

        // ---------------------------------------------------
        // Leave this to the user to commit it explicitly
        // ---------------------------------------------------
    }


}
