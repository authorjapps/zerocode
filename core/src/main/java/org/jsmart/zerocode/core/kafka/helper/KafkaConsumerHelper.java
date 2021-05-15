package org.jsmart.zerocode.core.kafka.helper;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.AVRO;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.DEFAULT_POLLING_TIME_MILLI_SEC;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.PROTO;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.common.KafkaCommonUtils.resolveValuePlaceHolders;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.KafkaConstants;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecords;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerRawRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

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

    public static ConsumerRecords initialPollWaitingForConsumerGroupJoin(Consumer consumer, ConsumerLocalConfigs effectiveLocalConfigs) {

            for (int run = 0; run < 50; run++) {
                if (!consumer.assignment().isEmpty()) {
                    return new ConsumerRecords(new HashMap());
                }
                ConsumerRecords records = consumer.poll(Duration.of(getPollTime(effectiveLocalConfigs), ChronoUnit.MILLIS));
                if (!records.isEmpty()) {
                    return records;
                }
            }

        throw new RuntimeException("\n********* Kafka Consumer unable to join in time - try increasing consumer polling time setting *********\n");
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
                    consumerCommon.getProtoClassType(),
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

        // Handle recordType
        String effectiveProtobufMessageClassType = ofNullable(consumerLocal.getProtoClassType()).orElse(consumerCommon.getProtoClassType());


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
                effectiveProtobufMessageClassType,
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
                                Iterator recordIterator, ConsumerLocalConfigs consumerLocalConfig) throws IOException {
        while (recordIterator.hasNext()) {
            ConsumerRecord thisRecord = (ConsumerRecord) recordIterator.next();

            Object key = thisRecord.key();
            Object valueObj = thisRecord.value();
            Headers headers = thisRecord.headers();
            String keyStr =  thisRecord.key() != null ?  thisRecord.key().toString() : "";
            String valueStr = consumerLocalConfig != null && KafkaConstants.PROTO.equalsIgnoreCase(consumerLocalConfig.getRecordType()) ? convertProtobufToJson(thisRecord, consumerLocalConfig) : valueObj.toString();
            LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}, Headers - {}",
                    key, valueStr, thisRecord.partition(), thisRecord.offset(), headers);

            JsonNode keyNode = objectMapper.readTree(keyStr);
            JsonNode valueNode = objectMapper.readTree(valueStr);

            Map<String, String> headersMap = null;
            if (headers != null) {
                headersMap = new HashMap<>();
                for (Header header : headers) {
                    headersMap.put(header.key(), new String(header.value()));
                }
            }
            ConsumerJsonRecord jsonRecord = new ConsumerJsonRecord(keyNode, valueNode, headersMap);
            jsonRecords.add(jsonRecord);
        }
    }

    private static String convertProtobufToJson(ConsumerRecord thisRecord, ConsumerLocalConfigs consumerLocalConfig) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(consumerLocalConfig.getProtoClassType())) {
            throw new IllegalArgumentException(
                    "[protoClassType] is required consumer config for recordType PROTO.");
        }
        MessageOrBuilder builderOrMessage = (MessageOrBuilder) createMessageOrBuilder(
                consumerLocalConfig.getProtoClassType(), (byte[]) thisRecord.value());
        try {
            return JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames().print(builderOrMessage);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static MessageOrBuilder createMessageOrBuilder(String messageClass, byte[] value) {
        try {
            Class<Message> msgClass = (Class<Message>) Class.forName(messageClass);
            Method method = msgClass.getMethod("parseFrom", new Class[]{byte[].class});
            return (MessageOrBuilder) method.invoke(null, value);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
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

        } else if (testConfigs != null && (JSON.equals(testConfigs.getRecordType()) || PROTO.equalsIgnoreCase(testConfigs.getRecordType()) || AVRO.equalsIgnoreCase(testConfigs.getRecordType()))) {
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
            String[] seekParts = effectiveLocal.getSeekTopicPartitionOffset();
            String topic = seekParts[0];
            int partition = parseInt(seekParts[1]);
            long offset = parseLong(seekParts[2]);

            TopicPartition topicPartition = new TopicPartition(topic, partition);
            Set<TopicPartition> topicPartitions = new HashSet<>();
            topicPartitions.add(topicPartition);

            consumer.unsubscribe();
            consumer.assign(topicPartitions);

            if (offset <= -1) {
                consumer.seekToEnd(topicPartitions);
                consumer.seek(topicPartition, consumer.position(topicPartition) + offset);
            } else {
                consumer.seek(topicPartition, offset);
            }
        }
    }

    private static void validateCommitFlags(Boolean commitSync, Boolean commitAsync) {
        if ((commitSync != null && commitAsync != null) && commitSync == true && commitAsync == true) {
            throw new RuntimeException("\n********* Both commitSync and commitAsync can not be true *********\n");
        }
    }

    private static void validateSeekConfig(ConsumerLocalConfigs localConfigs) {
        String seek = localConfigs.getSeek();
        if (!isEmpty(seek)) {
            String[] split = seek.split(",");
            if (split == null || split.length < 3) {
                throw new RuntimeException("\n------> 'seek' should contain 'topic,partition,offset' e.g. 'topic1,0,2' ");
            }
        }
    }
}
