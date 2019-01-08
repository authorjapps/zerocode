package org.jsmart.zerocode.core.kafka;

public interface KafkaConstants {
    String KAFKA_BROKERS = "192.168.99.1:9092";

    Integer MESSAGE_COUNT = 110;

    String CLIENT_ID = "client1";

    String TOPIC_NAME = "demo";

    String GROUP_ID_CONFIG = "consumerGroup10";

    Integer MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS = 5;

    Long DEFAULT_POLLING_TIME_MILLI_SEC = 100L;

    String OFFSET_RESET_LATEST = "latest";

    String OFFSET_RESET_EARLIER = "earliest";

    Integer MAX_POLL_RECORDS = 1;

    String RAW = "RAW";

    String JSON = "JSON";

    String RECORD_TYPE_JSON_PATH = "$.recordType";

}
