//package org.jsmart.zerocode.core.di.provider;
//
//import org.jsmart.zerocode.core.kafka.KafkaService;
//import org.jsmart.zerocode.core.kafka.KafkaServiceImpl;
//
//import javax.inject.Provider;
//
//public class KafkaServicesProvider implements Provider<KafkaService> {
//
//    @Override
//    public KafkaService get() {
//
//        KafkaService kafkaService = new KafkaServiceImpl();
//        // Read the properties etc
//
//        return kafkaService;
//    }
//
//}