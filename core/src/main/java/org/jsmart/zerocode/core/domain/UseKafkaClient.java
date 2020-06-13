package org.jsmart.zerocode.core.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UseKafkaClient {
    /**
     * @return a Kafka Client implementation class which will override the default implementation
     */
    Class<? extends BasicKafkaClient> value();
}