package org.jsmart.zerocode.core.constants;

import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;

public class ZerocodeConstants {
    public static final String PROPERTY_KEY_HOST = "restful.application.endpoint.host";
    public static final String PROPERTY_KEY_PORT = "restful.application.endpoint.context";

    public static final String KAFKA = "kafka";
    public static final String KAFKA_TOPIC = "kafka-topic:";
    public static final String OK = "Ok";
    public static final String FAILED = "Failed";

    public static final String DSL_FORMAT = readJsonAsString("dsl_formats/dsl_parameterized_values.json");
}
