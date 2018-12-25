package org.jsmart.zerocode.core.di.module;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;

public class RuntimeKafkaClientModule implements Module {

    private final Class<? extends BasicKafkaClient> customKafkaClientClazz;

    public RuntimeKafkaClientModule(Class<? extends BasicKafkaClient> customKafkaClientClazz) {
        this.customKafkaClientClazz = customKafkaClientClazz;
    }

    public void configure(Binder binder) {
        binder.bind(BasicKafkaClient.class).to(customKafkaClientClazz);
    }
}