package org.jsmart.zerocode.core.kafka;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class KafkaServiceImpl implements KafkaService {
    @Inject(optional = true)
    @Named("consumer.commitAsync")
    private boolean commitAsync;

    @Inject(optional = true)
    @Named("producer.key1")
    private String key1;


    @Override
    public String consume(String serviceName, String methodName, String requestJson) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> commitAsync = " + commitAsync);
        return null;
    }

    @Override
    public String produce(String kafkaServers, String topicName, String requestJson) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> key1 = " + key1);
        return "{\"status\": \"Ok\"}";
    }
}

