package org.jsmart.zerocode.core.kafka.receive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.JsonRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.time.Duration.ofMillis;
import static org.jsmart.zerocode.core.kafka.KafkaConstants.*;
import static org.jsmart.zerocode.core.kafka.helper.KafkaFileRecordHelper.handleRecordsDump;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.*;

@Singleton
public class KafkaReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReceiver.class);

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

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

                LOGGER.info("Consumer chosen recordType: " + effectiveLocal.getRecordType());

                switch (effectiveLocal.getRecordType()){
                    case RAW:
                        readRaw(rawRecords, recordIterator);
                        break;

                    case JSON:
                        readJson(jsonRecords, recordIterator);
                        break;

                    case RAW_AND_JSON:
                        readRawAndJson(rawRecords, jsonRecords, recordIterator);
                        break;

                    default:
                        throw new RuntimeException("Unsupported record type - " + effectiveLocal.getRecordType());
                }

            }

            handleCommitSyncAsync(consumer, effectiveLocal);
        }

        consumer.close();

        handleRecordsDump(effectiveLocal, rawRecords);

        return prepareResult(effectiveLocal, jsonRecords, rawRecords);

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

        // --------------------------------------------------------
        // Leave this to the user to "commit" the offset explicitly
        // --------------------------------------------------------
    }


}
