package org.jsmart.zerocode.core.kafka.receive;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.Duration.ofMillis;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.AVRO;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.JSON;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.RAW;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.PROTO;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.createConsumer;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.deriveEffectiveConfigs;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.getMaxTimeOuts;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.getPollTime;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.handleCommitSyncAsync;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.handleSeekOffset;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.initialPollWaitingForConsumerGroupJoin;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.prepareResult;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.readConsumerLocalTestProperties;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.readJson;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.readRaw;
import static org.jsmart.zerocode.core.kafka.helper.KafkaFileRecordHelper.handleRecordsDump;

@Singleton
public class KafkaReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReceiver.class);

    @Inject(optional = true)
    @Named("kafka.consumer.properties")
    private String consumerPropertyFile;

    @Inject
    private ConsumerCommonConfigs consumerCommonConfigs;

    public String receive(String kafkaServers, String topicName, String requestJsonWithConfig) throws IOException {

        ConsumerLocalConfigs consumerLocalConfigs = readConsumerLocalTestProperties(requestJsonWithConfig);

        ConsumerLocalConfigs effectiveLocal = deriveEffectiveConfigs(consumerLocalConfigs, consumerCommonConfigs);

        LOGGER.info("\n### Kafka Consumer Effective configs:{}\n", effectiveLocal);

        Consumer consumer = createConsumer(kafkaServers, consumerPropertyFile, topicName);

        final List<ConsumerRecord> rawRecords = new ArrayList<>();
        final List<ConsumerJsonRecord> jsonRecords = new ArrayList<>();

        int noOfTimeOuts = 0;

        handleSeekOffset(effectiveLocal, consumer);

        LOGGER.info("initial polling to trigger ConsumerGroupJoin");

        ConsumerRecords records = initialPollWaitingForConsumerGroupJoin(consumer, effectiveLocal);

        if(!records.isEmpty()) {
            LOGGER.info("Received {} records on initial poll\n", records.count());

            appendNewRecords(records, rawRecords, jsonRecords, effectiveLocal);

            handleCommitSyncAsync(consumer, consumerCommonConfigs, effectiveLocal);
        }

        while (noOfTimeOuts < getMaxTimeOuts(effectiveLocal)) {
            LOGGER.info("polling records  - noOfTimeOuts reached : " + noOfTimeOuts);

            records = consumer.poll(ofMillis(getPollTime(effectiveLocal)));
            noOfTimeOuts++;

            if (records.count() == 0) {
                continue;
            }

            LOGGER.info("Received {} records after {} timeouts\n", records.count(), noOfTimeOuts);

            appendNewRecords(records, rawRecords, jsonRecords, effectiveLocal);

            handleCommitSyncAsync(consumer, consumerCommonConfigs, effectiveLocal);

        }

        consumer.close();

        handleRecordsDump(effectiveLocal, rawRecords, jsonRecords);

        return prepareResult(effectiveLocal, jsonRecords, rawRecords);

    }

    private void appendNewRecords(ConsumerRecords records, List<ConsumerRecord> rawRecords, List<ConsumerJsonRecord> jsonRecords, ConsumerLocalConfigs effectiveLocal) throws IOException {
        Iterator recordIterator = records.iterator();

        LOGGER.info("Consumer chosen recordType: " + effectiveLocal.getRecordType());

        switch (effectiveLocal.getRecordType()) {
            case RAW:
                readRaw(rawRecords, recordIterator);
                break;
            case PROTO:
            case AVRO:
            case JSON:
                readJson(jsonRecords, recordIterator,effectiveLocal);
                break;

            default:
                throw new RuntimeException("Unsupported record type - '" + effectiveLocal.getRecordType()
                        + "'. Supported values are 'JSON','RAW'");
        }
    }

}
