package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.ConsumedRecords;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.JsonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Optional.ofNullable;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.DEFAULT_POLLING_TIME_MILLI_SEC;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class KafkaConsumerHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerHelper.class);
    private static final Gson gson = new GsonSerDeProvider().get();
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    public static Consumer createConsumer(String bootStrapServers, String consumerPropertyFile, String topic) {
        try (InputStream propsIs = Resources.getResource(consumerPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put("bootstrap.servers", bootStrapServers);

            final Consumer consumer = new KafkaConsumer(properties);
            consumer.subscribe(Collections.singletonList(topic));

            return consumer;

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka properties and creating a consumer- " + e);
        }
    }

    public static void validateConsumeProperties(ConsumerLocalConfigs consumeLocalTestProps) {
        if (null != consumeLocalTestProps.getFileDumpType() && consumeLocalTestProps.getFileDumpTo() == null) {
            throw new RuntimeException("Found type, but no fileName. Try e.g. 'fileDumpTo':'target/temp/abc.txt' ");
        }
    }

    private static void validateIfBothEnabled(Boolean commitSync, Boolean commitAsync) {
        if ((commitSync != null && commitAsync != null)  && commitSync == true && commitAsync == true) {
            throw new RuntimeException("\n********* Both commitSync and commitAsync can not be true *********\n");
        }
    }

    public static void validateLocalConfigs(ConsumerLocalConfigs consumeLocalTestProps) {
        if (consumeLocalTestProps != null) {
            Boolean localCommitSync = consumeLocalTestProps.getCommitSync();
            Boolean localCommitAsync = consumeLocalTestProps.getCommitAsync();

            validateIfBothEnabled(localCommitSync, localCommitAsync);
        }
    }

    public static void validateCommonConfigs(ConsumerCommonConfigs consumerCommonConfigs) {
        validateIfBothEnabled(consumerCommonConfigs.getCommitSync(), consumerCommonConfigs.getCommitAsync());
    }

    public static ConsumerLocalConfigs deriveEffectiveConfigs(ConsumerLocalConfigs consumerLocalTestConfigs, ConsumerCommonConfigs consumerCommonConfigs) {

        validateCommonConfigs(consumerCommonConfigs);
        validateLocalConfigs(consumerLocalTestConfigs);

        return createEffective(consumerCommonConfigs, consumerLocalTestConfigs);
    }

    public static ConsumerLocalConfigs createEffective(ConsumerCommonConfigs consumerCommon, ConsumerLocalConfigs consumerLocal) {
        if(consumerLocal == null){
            return new ConsumerLocalConfigs(
                    consumerCommon.getRecordType(),
                    consumerCommon.getFileDumpTo(),
                    consumerCommon.getFileDumpType(),
                    consumerCommon.getCommitAsync(),
                    consumerCommon.getCommitSync(),
                    consumerCommon.getShowConsumedRecords(),
                    consumerCommon.getMaxNoOfRetryPollsOrTimeouts(),
                    consumerCommon.getPollingTime());
        }

        // Handle recordType
        String effectiveRecordType = ofNullable(consumerLocal.getRecordType()).orElse(consumerCommon.getRecordType());

        // Handle fileDumpTo
        String effectiveFileDumpTo = ofNullable(consumerLocal.getFileDumpTo()).orElse(consumerCommon.getFileDumpTo());

        // Handle fileDumpType
        String effectiveFileDumpType = ofNullable(consumerLocal.getFileDumpType()).orElse(consumerCommon.getFileDumpType());

        // Handle showConsumedRecords
        Boolean effectiveShowConsumedRecords = ofNullable(consumerLocal.getShowConsumedRecords()).orElse(consumerCommon.getShowConsumedRecords());

        // Handle maxNoOfRetryPollsOrTimeouts
        Integer effectiveMaxNoOfRetryPollsOrTimeouts = ofNullable(consumerLocal.getMaxNoOfRetryPollsOrTimeouts()).orElse(consumerCommon.getMaxNoOfRetryPollsOrTimeouts());

        // Handle pollingTime
        Long effectivePollingTime = ofNullable(consumerLocal.getPollingTime()).orElse(consumerCommon.getPollingTime());

        // Handle commitSync and commitAsync -START
        Boolean effectiveCommitSync;
        Boolean effectiveCommitAsync;

        Boolean localCommitSync = consumerLocal.getCommitSync();
        Boolean localCommitAsync = consumerLocal.getCommitAsync();

        if (localCommitSync == null && localCommitAsync == null) {
            effectiveCommitSync = consumerCommon.getCommitSync();
            effectiveCommitAsync = consumerCommon.getCommitAsync();

        } else {
            effectiveCommitSync = localCommitSync;
            effectiveCommitAsync = localCommitAsync;
        }

        return new ConsumerLocalConfigs(
                effectiveRecordType,
                effectiveFileDumpTo,
                effectiveFileDumpType,
                effectiveCommitAsync,
                effectiveCommitSync,
                effectiveShowConsumedRecords,
                effectiveMaxNoOfRetryPollsOrTimeouts,
                effectivePollingTime);
    }

    public static ConsumerLocalConfigs readConsumerLocalTestProperties(String requestJsonWithConfigWrapped) {
        try {
            ConsumerLocalConfigsWrap consumerLocalConfigsWrap = (new ObjectMapperProvider().get())
                    .readValue(requestJsonWithConfigWrapped, ConsumerLocalConfigsWrap.class);

            return consumerLocalConfigsWrap.getConsumerLocalConfigs();

        } catch (IOException exx) {
            throw new RuntimeException(exx);
        }
    }

    public static Integer getMaxTimeOuts(ConsumerLocalConfigs effectiveLocalTestProps) {
        return ofNullable(effectiveLocalTestProps.getMaxNoOfRetryPollsOrTimeouts())
                .orElse(MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS);
    }

    public static Long getPollTime(ConsumerLocalConfigs effectiveLocal) {
        return ofNullable(effectiveLocal.getPollingTime())
                .orElse(DEFAULT_POLLING_TIME_MILLI_SEC);
    }

    public static void readRaw(ArrayList<ConsumerRecord> rawRecords, Iterator recordIterator) {
        while (recordIterator.hasNext()) {
            ConsumerRecord thisRecord = (ConsumerRecord)recordIterator.next();
            LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                    thisRecord.key(), thisRecord.value(), thisRecord.partition(), thisRecord.offset());
            rawRecords.add(thisRecord);
        }
    }

    public static void readJson(List<JsonRecord> jsonRecords,
                          Iterator recordIterator ) throws IOException {
        while (recordIterator.hasNext()) {
            ConsumerRecord thisRecord = (ConsumerRecord)recordIterator.next();

            Object key = thisRecord.key();
            Object value = thisRecord.value();
            LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                    key, value, thisRecord.partition(), thisRecord.offset());

            JsonNode valueNode = objectMapper.readTree(value.toString());
            JsonRecord jsonRecord = new JsonRecord(thisRecord.key(), null, valueNode);
            jsonRecords.add(jsonRecord);
        }
    }

    public static void readRawAndJson(ArrayList<ConsumerRecord> rawRecords,
                                      List<JsonRecord> jsonRecords,
                                      Iterator recordIterator) throws IOException {
        while (recordIterator.hasNext()) {
            ConsumerRecord thisRecord = (ConsumerRecord)recordIterator.next();

            // ----------------------
            // Add to raw record list
            // ----------------------
            LOGGER.info("Consumed - JSON record >> " + thisRecord);
            rawRecords.add(thisRecord);

            Object key = thisRecord.key();
            Object value = thisRecord.value();
            LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                    key, value, thisRecord.partition(), thisRecord.offset());

            JsonNode valueNode = objectMapper.readTree(value.toString());
            JsonRecord jsonRecord = new JsonRecord(thisRecord.key(), null, valueNode);
            // -----------------------
            // Add to json record list
            // -----------------------
            jsonRecords.add(jsonRecord);
        }
    }

    public static String prepareResult(ConsumerLocalConfigs consumeLocalTestProps,
                                 List<JsonRecord> jsonRecords,
                                 ArrayList<ConsumerRecord> rawRecords) throws JsonProcessingException {

        if (consumeLocalTestProps != null && consumeLocalTestProps.getShowConsumedRecords() == false) {
            return prettyPrintJson(gson.toJson(new ConsumedRecords(jsonRecords.size() == 0 ? rawRecords.size() : 0)));

        } else if (consumeLocalTestProps != null && "RAW".equals(consumeLocalTestProps.getRecordType())) {
            return prettyPrintJson(gson.toJson(new ConsumedRecords(rawRecords)));

        } else if (consumeLocalTestProps != null && "JSON".equals(consumeLocalTestProps.getRecordType())) {
            return prettyPrintJson(objectMapper.writeValueAsString(new ConsumedRecords(jsonRecords)));

        } else {
            // -------------------------------------------------
            // read type as "RAW, JSON" : Show both RAW and JSON
            // -------------------------------------------------
            return prettyPrintJson(gson.toJson(new ConsumedRecords(jsonRecords, rawRecords, null)));

        }
    }
}