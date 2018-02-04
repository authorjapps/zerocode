package org.jsmart.zerocode.core.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RunnerUtilsTest {


    @Test
    public void testSuffixEnvValue() throws Exception {

        assertThat(RunnerUtils.suffixEnvValue("abcd.properties", "_ci"), is("abcd_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("my_conf.properties", "_ci"), is("my_conf_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("myfolder/myapp.properties", "_ci"), is("myfolder/myapp_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("/myfolder/myapp.properties", "_ci"), is("/myfolder/myapp_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("/myfolder/myapp.properties", ""), is("/myfolder/myapp.properties"));

    }
}