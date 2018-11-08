package org.jsmart.zerocode.core.kafka.helper;

import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import static org.jsmart.zerocode.core.kafka.common.CommonConfigs.BOOTSTRAP_SERVERS;

public class KafkaHelper {
    public static Producer<Long, String> createProducer(String bootStrapServers, String producerPropertyFile) {

        try (InputStream propsIs = Resources.getResource(producerPropertyFile).openStream()) {
            Properties properties = new Properties();
            properties.load(propsIs);
            properties.put(BOOTSTRAP_SERVERS, bootStrapServers);

            return new KafkaProducer(properties);

        } catch (IOException e) {
            throw new RuntimeException("Exception while reading kafka producer properties" + e);
        }
    }

    public static Consumer<Long, String> createConsumer(String bootStrapServers, String consumerPropertyFile, String topic) {
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

    public static void validateConsumeProperties(ConsumerLocalConfigs consumeLocalTestProps) {
        if (null != consumeLocalTestProps.getFileDumpType() && consumeLocalTestProps.getFileDumpTo() == null) {
            throw new RuntimeException("Found type, but no fileName. Try e.g. 'fileDumpTo':'target/temp/abc.txt' ");
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

    private static void validateIfBothEnabled(Boolean commitSync, Boolean commitAsync) {
        if ((commitSync != null && commitAsync != null)  && commitSync == true && commitAsync == true) {
            throw new RuntimeException("\n********* Both commitSync and commitAsync can not be true *********\n");
        }
    }

}
