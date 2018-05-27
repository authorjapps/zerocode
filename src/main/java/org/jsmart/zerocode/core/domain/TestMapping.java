package org.jsmart.zerocode.core.domain;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable( value = TestMappings.class )
public @interface TestMapping {
    Class<?> testClass();
    String testMethod();
}