//package org.jsmart.zerocode.core.di.module;
//
//import com.google.inject.Binder;
//import com.google.inject.Module;
//import org.jsmart.zerocode.core.di.provider.KafkaServicesProvider;
//import org.jsmart.zerocode.core.kafka.KafkaService;
//
//import javax.inject.Singleton;
//
//public class KafkaModule implements Module {
//
//    public void configure(Binder binder) {
//        binder.bind(KafkaService.class).toProvider(KafkaServicesProvider.class).in(Singleton.class);
//    }
//}