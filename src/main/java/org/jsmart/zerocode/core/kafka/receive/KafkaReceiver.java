package org.jsmart.zerocode.core.kafka.receive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jsmart.zerocode.core.di.provider.GsonSerDeProvider;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.kafka.ConsumedRecords;
import org.jsmart.zerocode.core.kafka.DeliveryStatus;
import org.jsmart.zerocode.core.kafka.KafkaConstants;
import org.jsmart.zerocode.core.kafka.consume.ConsumeRequestConfig;
import org.jsmart.zerocode.core.kafka.consume.ConsumeTestProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static java.time.Duration.ofMillis;
import static org.jsmart.zerocode.core.domain.ZerocodeConstants.OK;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

@Singleton
public class KafkaReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReceiver.class);

    @Inject(optional = true)
    @Named("kafka.consumer.properties")
    private String consumerPropertyFile;

    private static ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private static Gson gson = new GsonSerDeProvider().get();

    public String receive(String kafkaServers, String topicName, String consumePropertiesAsJson) throws IOException {

        ConsumeTestProperties consumeLocalTestProps = readConsumerLocalTestProperties(consumePropertiesAsJson);

        Consumer<Long, String> consumer = createConsumer(kafkaServers, topicName);

        final List<ConsumerRecord> fetchedRecords = new ArrayList<>();

        int noOfTimeOuts = 0;

        while (true) {
            final ConsumerRecords<Long, String> records = consumer.poll(ofMillis(100));

            String jsonRecords = gson.toJson(records);
            System.out.println("jsonRecords>>>>>>>>>>\n" + jsonRecords);

            if (records.count() == 0) {
                noOfTimeOuts++;
                //TODO- make this configurable
                if (noOfTimeOuts > KafkaConstants.MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS) {
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

            if (consumeLocalTestProps != null && consumeLocalTestProps.getCommitAsync() == true) {
                consumer.commitAsync();

//            } else if (false) { // TODO- commitSync
            } else {
                consumer.commitSync();
            }
        }

        consumer.close();

        String fileDumpType = consumeLocalTestProps != null ? consumeLocalTestProps.getFileDumpType() : null;
        if (fileDumpType != null) {
            switch (fileDumpType) {
                case "RAW":
                    validateConsumeProperties(consumeLocalTestProps);
                    dumpRecords(consumeLocalTestProps.getFileDumpTo(), fetchedRecords);
                    break;

                case "JSON":
                    // dump JSON data to a file
                    break;

                default:
                    throw new RuntimeException("Unsupported fileDumpType - '" + fileDumpType + "'");
            }
        }

        if (consumeLocalTestProps != null && consumeLocalTestProps.getShowRecordsAsResponse() == false) {
            return objectMapper.writeValueAsString(new DeliveryStatus(OK, fetchedRecords.size()));
        }

        ConsumedRecords consumedRecords = new ConsumedRecords(fetchedRecords);

        //TODO - Can inject this Gson
        String jsonAsString = gson.toJson(consumedRecords);
        String consoleJson = prettyPrintJson(jsonAsString);

        return consoleJson;
    }

    public static void validateConsumeProperties(ConsumeTestProperties consumeLocalTestProps) {
        if (null != consumeLocalTestProps.getFileDumpType() && consumeLocalTestProps.getFileDumpTo() == null) {
            throw new RuntimeException("Found type, but no fileName. Try e.g. 'fileDumpTo':'target/temp/abc.txt' ");
        }
    }

    public static ConsumeTestProperties readConsumerLocalTestProperties(String consumePropertiesAsJson) {
        try {
            ConsumeRequestConfig consumeRequestConfig = objectMapper.readValue(consumePropertiesAsJson, ConsumeRequestConfig.class);

            return consumeRequestConfig.getConsumeTestProperties();

        } catch (IOException exx) {
            throw new RuntimeException(exx);
        }
    }

    private Consumer<Long, String> createConsumer(String bootStrapServers, String topic) {
        try (InputStream propsIs = Resources.getResource(consumerPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put("bootstrap.servers", bootStrapServers);

            final Consumer<Long, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Collections.singletonList(topic));
            return consumer;

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka properties" + e);
        }
    }


    //TODO - take it to File utils
    static void dumpRecords(String fileName, List<ConsumerRecord> fetchedRecords) {

        File file = createCascadeIfNotExisting(fileName);

        try {
            FileWriter writer = new FileWriter(file.getAbsoluteFile());

            for (ConsumerRecord aRecord : fetchedRecords) {
                String key = aRecord.key() != null ? aRecord.key().toString() : "";
                String value = aRecord.value() != null ? aRecord.value().toString() : "";
                writer.write(key + value + osIndependentNewLine());
            }
            writer.close();
        } catch (IOException exx) {
            throw new RuntimeException("Could not write to file '" + fileName + "' exception >> " + exx);
        }
    }

    static File createCascadeIfNotExisting(String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.createDirectories(path.getParent());

            File file = new File(fileName);

            return file;
        } catch (IOException exx) {
            throw new RuntimeException("Create file '" + fileName + "' Exception" + exx);
        }
    }

    private static String osIndependentNewLine() {
        return System.getProperty("line.separator");
    }
}
