package org.jsmart.zerocode.core.constants;

import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;

public interface ZerocodeConstants {
    String PROPERTY_KEY_HOST = "restful.application.endpoint.host";
    String PROPERTY_KEY_PORT = "restful.application.endpoint.context";

    String KAFKA = "kafka";
    String KAFKA_TOPIC = "kafka-topic:";
    String S3 = "s3";
    String S3_BUCKET = "s3-bucket:";
    String OK = "Ok";
    String FAILED = "Failed";

    String DSL_FORMAT = readJsonAsString("dsl_formats/dsl_parameterized_values.json");
}
