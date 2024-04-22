package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.jayway.jsonpath.JsonPath;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.KafkaConstants;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.consume.SeekTimestamp;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecords;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerRawRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.AVRO;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.DEFAULT_POLLING_TIME_MILLI_SEC;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.PROTO;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.common.KafkaCommonUtils.resolveValuePlaceHolders;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class KafkaConsumerHelper {
    public static final String CONSUMER = "CONSUMER";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerHelper.class);
    private static final Gson gson = new GsonSerDeProvider().get();
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    public static Map<String, Consumer> consumerCacheByTopicMap = new HashMap<>();

    public static Consumer createConsumer(String bootStrapServers, String consumerPropertyFile, String topic, Boolean consumerToBeCached) {
        Consumer sameConsumer = getCachedConsumer(topic, consumerToBeCached);
        if (sameConsumer != null) {
            return sameConsumer;
        }

        try (InputStream propsIs = Resources.getResource(consumerPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put("bootstrap.servers", bootStrapServers);

            resolveValuePlaceHolders(properties);

            final Consumer consumer = new KafkaConsumer(properties);

            if (consumerToBeCached) {
                consumerCacheByTopicMap.forEach((xTopic, xConsumer) -> {
                    if (!xTopic.equals(topic)) {
                        // close the earlier consumer if in the same group for safety.
                        // (even if not in the same group, closing it anyway will not do any harm)
                        // Otherwise rebalance will fail while rejoining/joining the same group for a new consumer
                        // i.e. because old consumer(xConsumer) is still consuming,
                        // and has not let GC know that it has stopped consuming or not sent any LeaveGroup request.
                        // If you have a single(0) partition topic in your Kafka Broker, xConsumer is still holding it,
                        // i.e. not yet unassigned.
                        // Note- It works fine and not required to close() if the new consumer joining the same Group for the same topic.
                        xConsumer.close();
                    }
                });
                // Remove the earlier topic-consumer from the cache.
                // Recreate will happen above anyway if not found in cache via "new KafkaConsumer(properties)".
                consumerCacheByTopicMap.entrySet().removeIf(xTopic -> !xTopic.equals(topic));

                consumerCacheByTopicMap.put(topic, consumer);
            }

            return consumer;

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka properties and creating a consumer- " + e);
        }
    }

    public static ConsumerRecords initialPollWaitingForConsumerGroupJoin(Consumer consumer, ConsumerLocalConfigs effectiveLocalConfigs) {

        for (int run = 0; run < 50; run++) {
            if (!consumer.assignment().isEmpty()) {
                LOGGER.debug("==> WaitingForConsumerGroupJoin - Partition now assigned. No records yet consumed");
                return new ConsumerRecords(new HashMap());
            }
            LOGGER.debug("==> WaitingForConsumerGroupJoin - Partition not assigned. Polling once");
            ConsumerRecords records = consumer.poll(Duration.of(getPollTime(effectiveLocalConfigs), ChronoUnit.MILLIS));
            LOGGER.debug("==> WaitingForConsumerGroupJoin - polled records length={}", records.count());
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
            validateSeekToTimestamp(localConfigs);
        }
    }

    private static void validateSeekToTimestamp(ConsumerLocalConfigs localConfigs) {
        String seekToTimestamp = localConfigs.getSeekEpoch();
        if (isEmpty(seekToTimestamp)) {
            if (isNumeric(seekToTimestamp) && (Long.parseLong(seekToTimestamp) > System.currentTimeMillis() || Long.parseLong(seekToTimestamp) < 0L)) {
                throw new RuntimeException("\n------> 'seekEpoch' is not a valid epoch/Unix timestamp");
            }
            if (!isEmpty(localConfigs.getSeek()) && Objects.nonNull(localConfigs.getSeekTimestamp())) {
                throw new RuntimeException("Only one of 'seek', 'seekEpoch' and 'seekTimestamp' should be provided, but not both. Please fix and rerun");
            }
        }
        if (Objects.nonNull(localConfigs.getSeekTimestamp())) {
            DateFormat dateFormat = new SimpleDateFormat(localConfigs.getSeekTimestamp().getFormat());
            try {
                Date date = dateFormat.parse(localConfigs.getSeekTimestamp().getTimestamp());
                long epochMillis = date.toInstant().toEpochMilli();
                if (epochMillis > System.currentTimeMillis() || epochMillis < 0L) {
                    throw new RuntimeException("\n------> 'seekTimestamp' is not a valid epoch/Unix timestamp " + epochMillis);
                }
            } catch (ParseException e) {
                throw new RuntimeException("Timestamp and format provided in 'seekTimestamp' cannot be parsed ", e);
            }
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
                    consumerCommon.getCacheByTopic(),
                    consumerCommon.getFilterByJsonPath(),
                    null,
                    null,
                    null);
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
        String filterByJsonPath = ofNullable(consumerLocal.getFilterByJsonPath()).orElse(consumerCommon.getFilterByJsonPath());

        // Handle consumerCache by topic
        Boolean effectiveConsumerCacheByTopic = ofNullable(consumerLocal.getCacheByTopic())
                .orElse(consumerCommon.getCacheByTopic());

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
                effectiveConsumerCacheByTopic,
                filterByJsonPath,
                consumerLocal.getSeek(),
                consumerLocal.getSeekEpoch(),
                consumerLocal.getSeekTimestamp());
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
            LOGGER.debug("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
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
            String keyStr = thisRecord.key() != null ? thisRecord.key().toString() : "";
            String valueStr = consumerLocalConfig != null && KafkaConstants.PROTO.equalsIgnoreCase(consumerLocalConfig.getRecordType()) ? convertProtobufToJson(thisRecord, consumerLocalConfig) : valueObj.toString();
            LOGGER.debug("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}, Headers - {}",
                    key, valueStr, thisRecord.partition(), thisRecord.offset(), headers);

            if (!isKeyParseableAsJson(keyStr)) {
                LOGGER.info(">>>Converting the key to JSON format for to able to read it");
                // Most cases a bare string is used as key, not really a JSON.
                // Hence, convert the key to JSON equivalent string for the framework able to
                // read the already consumed key for display and assertion purpose.
                keyStr = objectMapper.writeValueAsString(keyStr);
            }

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

    public static boolean isKeyParseableAsJson(String consumedKey) {
        try {
            objectMapper.readTree(consumedKey);
        } catch (JacksonException e) {
            LOGGER.info(">>>The key was not in a parsable JSON format:{}", consumedKey);
            return false;
        }
        LOGGER.info(">>> The consumed key was fine and parseable:{}", consumedKey);
        return true;
    }

    private static String convertProtobufToJson(ConsumerRecord thisRecord, ConsumerLocalConfigs consumerLocalConfig) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(consumerLocalConfig.getProtoClassType())) {
            throw new IllegalArgumentException(
                    "[protoClassType] is required consumer config for recordType PROTO.");
        }
        MessageOrBuilder builderOrMessage = createMessageOrBuilder(
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
            Method method = msgClass.getMethod("parseFrom", byte[].class);
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

        if (testConfigs != null && !testConfigs.getShowRecordsConsumed()) {
            int size = jsonRecords.size();
            result = prettyPrintJson(gson.toJson(new ConsumerRawRecords(size == 0 ? rawRecords.size() : size)));

        } else if (testConfigs != null && RAW.equals(testConfigs.getRecordType())) {
            result = prettyPrintJson(gson.toJson(new ConsumerRawRecords(rawRecords)));

        } else if (testConfigs != null && (JSON.equals(testConfigs.getRecordType()) || PROTO.equalsIgnoreCase(testConfigs.getRecordType()) || AVRO.equalsIgnoreCase(testConfigs.getRecordType()))) {
            result = prettyPrintJson(objectMapper.writeValueAsString(new ConsumerJsonRecords(jsonRecords)));

        } else {
            result = "{\"error\" : \"recordType Undecided, Please chose recordType as JSON or RAW\"}";
        }

        // Optional filter applied. if not supplied, original result is returned as response
        if (testConfigs != null && testConfigs.getFilterByJsonPath() != null) {
            String filteredResult = JsonPath.read(result, testConfigs.getFilterByJsonPath()).toString();
            List<ConsumerJsonRecord> filteredRecords = objectMapper.readValue(filteredResult, List.class);
            result = prettyPrintJson(objectMapper.writeValueAsString(new ConsumerJsonRecords(filteredRecords)));
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

        if (effectiveCommitSync != null && effectiveCommitSync) {
            consumer.commitSync();

        } else if (effectiveCommitAsync != null && effectiveCommitAsync) {
            consumer.commitAsync();

        } else {
            LOGGER.warn("Kafka client neither configured for `commitAsync()` nor `commitSync()`");
        }

        // --------------------------------------------------------
        // Leave this to the user to "commit" the offset explicitly
        // --------------------------------------------------------
    }

    public static void handleSeek(ConsumerLocalConfigs effectiveLocal, Consumer consumer, String topicName) {
        if (!isEmpty(effectiveLocal.getSeek())) {
            handleSeekByOffset(effectiveLocal, consumer);
        } else if (!isEmpty(effectiveLocal.getSeekEpoch())) {
            handleSeekByEpoch(Long.parseLong(effectiveLocal.getSeekEpoch()), consumer, topicName);
        } else if (Objects.nonNull(effectiveLocal.getSeekTimestamp())) {
            handleSeekByTimestamp(effectiveLocal.getSeekTimestamp(), consumer, topicName);
        }
    }

    private static void handleSeekByTimestamp(SeekTimestamp seekTimestamp, Consumer consumer, String topicName) {
        if (Objects.nonNull(seekTimestamp)) {
            DateFormat dateFormat = new SimpleDateFormat(seekTimestamp.getFormat());
            Date date = null;
            try {
                date = dateFormat.parse(seekTimestamp.getTimestamp());
            } catch (ParseException e) {
                throw new RuntimeException("Could not parse timestamp", e);
            }
            handleSeekByEpoch(date.toInstant().toEpochMilli(), consumer, topicName);
        }
    }

    private static void handleSeekByEpoch(Long epoch, Consumer consumer, String topicName) {
        if (Objects.nonNull(epoch)) {
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);

            //fetch partitions on topic
            List<TopicPartition> topicPartitions = partitionInfos.stream()
                    .map(info -> new TopicPartition(info.topic(), info.partition()))
                    .collect(Collectors.toList());

            //fetch offsets for each partition-timestamp pair
            Map<TopicPartition, Long> topicPartitionTimestampMap = topicPartitions.stream()
                    .collect(Collectors.toMap(Function.identity(), ignore -> epoch));
            Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestampMap = consumer.offsetsForTimes(topicPartitionTimestampMap);

            //assign to fetched partitions
            if (consumer.assignment().isEmpty()) {
                consumer.assign(topicPartitionOffsetAndTimestampMap.keySet());
            }

            //seek to end for partitions that have no offset/timestamp >= seekEpoch
            List<TopicPartition> noSeekPartitions = topicPartitionOffsetAndTimestampMap.entrySet().stream()
                    .filter(tp -> tp.getValue() == null)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (!noSeekPartitions.isEmpty()) {
                consumer.seekToEnd(noSeekPartitions);
                //commit the latest offsets so that they are skipped and only new messages consumed
                Map<TopicPartition, OffsetAndMetadata> partitionLatestOffsetsToCommit =
                        noSeekPartitions.stream()
                                .collect(Collectors.toMap(Function.identity(), tp -> new OffsetAndMetadata(consumer.position(tp) + 1)));
                LOGGER.debug("==> Committing the following : " + partitionLatestOffsetsToCommit);
                consumer.commitSync(partitionLatestOffsetsToCommit);
            }


            //seek to fetched offsets for partitions
            for (Map.Entry<TopicPartition, OffsetAndTimestamp> topicOffsetEntry : topicPartitionOffsetAndTimestampMap.entrySet()) {
                if (Objects.nonNull(topicOffsetEntry.getValue())) {
                    //seek to offset only if it is more than current offset position(for retry poll scenarios)
                    if (consumer.position(topicOffsetEntry.getKey()) < topicOffsetEntry.getValue().offset())
                        consumer.seek(topicOffsetEntry.getKey(), topicOffsetEntry.getValue().offset());
                    LOGGER.debug("==> Seeking to " + topicOffsetEntry);
                }
            }
        }
    }

    private static void handleSeekByOffset(ConsumerLocalConfigs effectiveLocal, Consumer consumer) {
        String[] seekParts = effectiveLocal.getSeekTopicPartitionOffset();
        String topic = seekParts[0];
        int partition = parseInt(seekParts[1]);
        long offset = parseLong(seekParts[2]);

        TopicPartition topicPartition = new TopicPartition(topic, partition);
        Set<TopicPartition> topicPartitions = new HashSet<>();
        topicPartitions.add(topicPartition);

        consumer.assign(topicPartitions);

        if (offset <= -1) {
            consumer.seekToEnd(topicPartitions);
            consumer.seek(topicPartition, consumer.position(topicPartition) + offset);
        } else {
            consumer.seek(topicPartition, offset);
        }
    }

    private static void validateCommitFlags(Boolean commitSync, Boolean commitAsync) {
        if ((commitSync != null && commitAsync != null) && commitSync && commitAsync) {
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

    private static Consumer getCachedConsumer(String topic, Boolean consumerToBeCached) {
        if (consumerToBeCached) {
            return consumerCacheByTopicMap.get(topic);
        }
        return null;
    }

}
