package org.jsmart.zerocode.core.kafka;

public interface KafkaConstants {
	public static String KAFKA_BROKERS = "192.168.99.1:9092";
	
	public static Integer MESSAGE_COUNT=110;
	
	public static String CLIENT_ID="client1";
	
	public static String TOPIC_NAME="demo";
	
	public static String GROUP_ID_CONFIG="consumerGroup10";
	
	public static Integer MAX_NO_MESSAGE_FOUND_COUNT=5;

	public static Integer MAX_NO_OF_RETRY_POLLS_OR_TIME_OUTS =5;

	public static String OFFSET_RESET_LATEST="latest";
	
	public static String OFFSET_RESET_EARLIER="earliest";
	
	public static Integer MAX_POLL_RECORDS=1;
}
