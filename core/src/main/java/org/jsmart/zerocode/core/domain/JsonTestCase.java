package org.jsmart.zerocode.core.domain;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Repeatable( value = JsonTestCases.class )
public @interface JsonTestCase {
    String value();
}
