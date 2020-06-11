package org.jsmart.zerocode.core.utils;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.utils.EnvUtils.getEnvValueInt;
import static org.jsmart.zerocode.core.utils.EnvUtils.getEnvValueString;
import static org.junit.Assert.*;

public class EnvUtilsTest {

    @Test
    public void test_getEnvValue_int() {
        System.setProperty("users", "5");
        Integer users = getEnvValueInt("users");
        assertThat(users, is(5));
    }

    @Test
    public void test_getEnvValue_string() {
        System.setProperty("users", "5");
        String users = getEnvValueString("users");
        assertThat(users, is("5"));
    }

    @Test
    public void test_getEnvValue_notYetSet() {
        //System.setProperty("users", "5"); //not set
        Integer users = getEnvValueInt("users");
        assertThat(users == null, is(true));
    }

}