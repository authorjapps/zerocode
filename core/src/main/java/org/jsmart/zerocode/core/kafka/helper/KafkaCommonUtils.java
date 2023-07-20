package org.jsmart.zerocode.core.kafka.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class KafkaCommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerHelper.class);

    public static void printBrokerProperties(String kafkaServers) {

        LOGGER.debug("\n---------------------------------------------------------\n" +
                format("kafka.bootstrap.servers - %s", kafkaServers) +
                "\n---------------------------------------------------------");

    }


}
