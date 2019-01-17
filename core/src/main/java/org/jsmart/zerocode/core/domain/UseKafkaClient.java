package org.jsmart.zerocode.core.domain;

import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UseKafkaClient {
    /**
     * @return a Http Client implementation class which will override the default implementation of RestEasy client
     */
    Class<? extends BasicKafkaClient> value();
}