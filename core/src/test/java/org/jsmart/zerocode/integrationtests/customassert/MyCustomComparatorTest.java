package org.jsmart.zerocode.integrationtests.customassert;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class MyCustomComparatorTest {
    @Test
    public void test_reflection_oneParam() throws Exception {
        java.lang.reflect.Method method;
        Object result;
        Class<?> clazz;
        clazz = Class.forName("org.jsmart.zerocode.integrationtests.customassert.MyCustomComparator");

        method = clazz.getMethod("strLen", String.class);
        result = method.invoke(MyCustomComparator.class, "Hello");
        assertThat(result, is(6));
    }

    @Test
    public void test_reflection_twoParams() throws Exception {
        java.lang.reflect.Method method;
        Object result;
        Class<?> clazz;
        clazz = Class.forName("org.jsmart.zerocode.integrationtests.customassert.MyCustomComparator");

        method = clazz.getMethod("strLen", String.class, String.class);
        result = method.invoke(MyCustomComparator.class, "Hello", "Hello2");
        assertThat(result, is(7));
    }

    @Test
    public void test_reflection_longParam() throws Exception {
        java.lang.reflect.Method method;
        Object result;
        Class<?> clazz;

        clazz = Class.forName("java.lang.Thread");
        method = clazz.getMethod("sleep", long.class);
        result = method.invoke(Thread.class, 2000L);
        assertThat(result, nullValue());
    }
}
