package org.jsmart.zerocode.core.domain;


import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable( value = TestMappings.class )
public @interface TestMapping {
    Class<?> testClass();
    String testMethod();
}