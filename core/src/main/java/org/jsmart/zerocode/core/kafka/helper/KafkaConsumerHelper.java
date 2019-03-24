package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecords;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerRawRecords;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.*;
import static org.jsmart.zerocode.core.kafka.common.KafkaCommonUtils.resolveValuePlaceHolders;
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

            resolveValuePlaceHolders(properties);

            final Consumer consumer = new KafkaConsumer(properties);
            consumer.subscribe(Collections.singletonList(topic));

            return consumer;

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka properties and creating a consumer- " + e);
        }
    }

    public static void validateLocalConfigs(ConsumerLocalConfigs localConfigs) {
        if (localConfigs != null) {
            Boolean localCommitSync = localConfigs.getCommitSync();
            Boolean localCommitAsync = localConfigs.getCommitAsync();
            validateCommitFlags(localCommitSync, localCommitAsync);

            validateSeekConfig(localConfigs);
        }
    }

    public static void validateCommonConfigs(ConsumerCommonConfigs consumerCommonConfigs) {
        validateCommitFlags(consumerCommonConfigs.getCommitSync(), consumerCommonConfigs.getCommitAsync());
    }

    public static ConsumerLocalConfigs deriveEffectiveConfigs(ConsumerLocalConfigs consumerLocalTestConfigs, ConsumerCommonConfigs consumerCommonConfigs) {

        validateCommonConfigs(consumerCommonConfigs);
        validateLocalConfigs(consumerLocalTestConfigs);

        return createEffective(consumerCommonConfigs, consumerLocalTestConfigs);
    }

    public static ConsumerLocalConfigs createEffective(ConsumerCommonConfigs consumerCommon, ConsumerLocalConfigs consumerLocal) {
        if (consumerLocal == null) {
            return new ConsumerLocalConfigs(
                    consumerCommon.getRecordType(),
                    consumerCommon.getFileDumpTo(),
                    consumerCommon.getCommitAsync(),
                    consumerCommon.getCommitSync(),
                    consumerCommon.getShowRecordsConsumed(),
                    consumerCommon.getMaxNoOfRetryPollsOrTimeouts(),
                    consumerCommon.getPollingTime(),
                    consumerCommon.getSeek());
        }

        // Handle recordType
        String effectiveRecordType = ofNullable(consumerLocal.getRecordType()).orElse(consumerCommon.getRecordType());

        // Handle fileDumpTo
        String effectiveFileDumpTo = ofNullable(consumerLocal.getFileDumpTo()).orElse(consumerCommon.getFileDumpTo());

        // Handle showRecordsConsumed
        Boolean effectiveShowRecordsConsumed = ofNullable(consumerLocal.getShowRecordsConsumed()).orElse(consumerCommon.getShowRecordsConsumed());

        // Handle maxNoOfRetryPollsOrTimeouts
        Integer effectiveMaxNoOfRetryPollsOrTimeouts = ofNullable(consumerLocal.getMaxNoOfRetryPollsOrTimeouts()).orElse(consumerCommon.getMaxNoOfRetryPollsOrTimeouts());

        // Handle pollingTime
        Long effectivePollingTime = ofNullable(consumerLocal.getPollingTime()).orElse(consumerCommon.getPollingTime());

        // Handle pollingTime
        String effectiveSeek = ofNullable(consumerLocal.getSeek()).orElse(consumerCommon.getSeek());

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
                effectiveCommitAsync,
                effectiveCommitSync,
                effectiveShowRecordsConsumed,
                effectiveMaxNoOfRetryPollsOrTimeouts,
                effectivePollingTime,
                effectiveSeek);
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

    public static void readRaw(List<ConsumerRecord> rawRecords, Iterator recordIterator) {
        while (recordIterator.hasNext()) {
            ConsumerRecord thisRecord = (ConsumerRecord) recordIterator.next();
            LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                    thisRecord.key(), thisRecord.value(), thisRecord.partition(), thisRecord.offset());
            rawRecords.add(thisRecord);
        }
    }

    public static void readJson(List<ConsumerJsonRecord> jsonRecords,
                                Iterator recordIterator) throws IOException {
        while (recordIterator.hasNext()) {
            ConsumerRecord thisRecord = (ConsumerRecord) recordIterator.next();

            Object key = thisRecord.key();
            Object value = thisRecord.value();
            LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                    key, value, thisRecord.partition(), thisRecord.offset());

            JsonNode valueNode = objectMapper.readTree(value.toString());
            ConsumerJsonRecord jsonRecord = new ConsumerJsonRecord(thisRecord.key(), null, valueNode);
            jsonRecords.add(jsonRecord);
        }
    }

    public static String prepareResult(ConsumerLocalConfigs testConfigs,
                                       List<ConsumerJsonRecord> jsonRecords,
                                       List<ConsumerRecord> rawRecords) throws JsonProcessingException {

        String result;

        if (testConfigs != null && testConfigs.getShowRecordsConsumed() == false) {
            int size = jsonRecords.size();
            result = prettyPrintJson(gson.toJson(new ConsumerRawRecords(size == 0 ? rawRecords.size() : size)));

        } else if (testConfigs != null && RAW.equals(testConfigs.getRecordType())) {
            result = prettyPrintJson(gson.toJson(new ConsumerRawRecords(rawRecords)));

        } else if (testConfigs != null && JSON.equals(testConfigs.getRecordType())) {
            result = prettyPrintJson(objectMapper.writeValueAsString(new ConsumerJsonRecords(jsonRecords)));

        } else {
            result = "{\"error\" : \"recordType Undecided, Please chose recordType as JSON or RAW\"}";
        }

        return result;
    }

    public static void handleCommitSyncAsync(Consumer<Long, String> consumer,
                                             ConsumerCommonConfigs consumerCommonConfigs,
                                             ConsumerLocalConfigs consumeLocalTestProps) {
        if (consumeLocalTestProps == null) {
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

        // --------------------------------------------------------
        // Leave this to the user to "commit" the offset explicitly
        // --------------------------------------------------------
    }

    public static void handleSeekOffset(ConsumerLocalConfigs effectiveLocal, Consumer consumer) {
        String seek = effectiveLocal.getSeek();
        if (!isEmpty(seek)) {
            String[] seekPosition = effectiveLocal.getSeekTopicPartitionOffset();
            TopicPartition topicPartition = new TopicPartition(seekPosition[0], parseInt(seekPosition[1]));

            Set<TopicPartition> topicPartitions = new HashSet<>();
            topicPartitions.add(topicPartition);

            consumer.unsubscribe();
            consumer.assign(topicPartitions);
            consumer.seek(topicPartition, parseLong(seekPosition[2]));
        }
    }

    private static void validateCommitFlags(Boolean commitSync, Boolean commitAsync) {
        if ((commitSync != null && commitAsync != null) && commitSync == true && commitAsync == true) {
            throw new RuntimeException("\n********* Both commitSync and commitAsync can not be true *********\n");
        }
    }

    private static void validateSeekConfig(ConsumerLocalConfigs localConfigs) {
        String seek = localConfigs.getSeek();
        if(!isEmpty(seek)) {
            String[] split = seek.split(",");
            if(split == null || split.length < 3) {
                throw new RuntimeException("\n------> 'seek' should contain 'topic,partition,offset' e.g. 'topic1,0,2' ");
            }
        }
    }
}
