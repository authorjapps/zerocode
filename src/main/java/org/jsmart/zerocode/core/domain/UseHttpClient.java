package org.jsmart.zerocode.core.domain;

import org.jsmart.zerocode.core.httpclient.BasicHttpClient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface UseHttpClient {
    /**
     * @return a Http Client implementation class which will override the default implementation of RestEasy client
     */
    Class<? extends BasicHttpClient> value();
}