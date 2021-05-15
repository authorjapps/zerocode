package org.jsmart.zerocode.core.kafka;

public interface KafkaConstants {

    Integer MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS = 5;

    Long DEFAULT_POLLING_TIME_MILLI_SEC = 100L;

    String RAW = "RAW";

    String JSON = "JSON";

    String AVRO = "AVRO";

    String PROTO = "PROTO";

    String RECORD_TYPE_JSON_PATH = "$.recordType";
    
    String PROTO_BUF_MESSAGE_CLASS_TYPE = "$.protoClassType";

}
