package org.jsmart.zerocode.core.kafka.receive;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.jsmart.zerocode.core.kafka.DeliveryStatus;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofMillis;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.OK;
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

        Consumer<Long, String> consumer = createConsumer(kafkaServers, consumerPropertyFile, topicName);

        final List<ConsumerRecord> fetchedRecords = new ArrayList<>();

        int noOfTimeOuts = 0;

        while (true) {
            LOGGER.info("polling records  - noOfTimeOuts reached : " + noOfTimeOuts);

            final ConsumerRecords<Long, String> records = consumer.poll(ofMillis(getPollTime(effectiveLocal)));

            //String jsonRecords = gson.toJson(records);
            //System.out.println("jsonRecords>>>>>>>>>>\n" + jsonRecords);

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
                records.forEach(thisRecord -> {
                    fetchedRecords.add(thisRecord);
                    LOGGER.info("\nRecord Key - {} , Record value - {}, Record partition - {}, Record offset - {}",
                            thisRecord.key(), thisRecord.value(), thisRecord.partition(), thisRecord.offset());
                });
            }

            handleCommitSyncAsync(consumer, effectiveLocal);
        }

        consumer.close();

        handleRecordsDump(effectiveLocal, fetchedRecords);

        return prepareResult(effectiveLocal, fetchedRecords);

    }

    private String prepareResult(ConsumerLocalConfigs consumeLocalTestProps, List<ConsumerRecord> fetchedRecords) throws JsonProcessingException {
        if (consumeLocalTestProps != null && !consumeLocalTestProps.getShowRecordsInResponse()) {

            return objectMapper.writeValueAsString(new DeliveryStatus(OK, fetchedRecords.size()));

        } else {
            //TODO - inject this Gson
            return prettyPrintJson(gson.toJson(new ConsumedRecords(fetchedRecords)));

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
