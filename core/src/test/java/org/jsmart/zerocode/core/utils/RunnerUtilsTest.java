package org.jsmart.zerocode.core.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getFullyQualifiedUrl;

public class RunnerUtilsTest {


    @Test
    public void testSuffixEnvValue() throws Exception {

        assertThat(RunnerUtils.suffixEnvValue("abcd.properties", "_ci"), is("abcd_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("my_conf.properties", "_ci"), is("my_conf_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("myfolder/myapp.properties", "_ci"), is("myfolder/myapp_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("/myfolder/myapp.properties", "_ci"), is("/myfolder/myapp_ci.properties"));

        assertThat(RunnerUtils.suffixEnvValue("/myfolder/myapp.properties", ""), is("/myfolder/myapp.properties"));

    }

    @Test
    public void test_Fqdn() {
        String fullyQualifiedUrl = getFullyQualifiedUrl("/abc",
                "http://aws-host.com",
                "8080",
                "/acontext");
        assertThat(fullyQualifiedUrl, is("http://aws-host.com:8080/acontext/abc"));

        // Host having http/https
        fullyQualifiedUrl = getFullyQualifiedUrl("http://aws-host.com:8080/acontext/abc",
                "asdf",
                "9090",
                "asdf");
        assertThat(fullyQualifiedUrl, is("http://aws-host.com:8080/acontext/abc"));

        // Without port -http
        fullyQualifiedUrl = getFullyQualifiedUrl("/abc",
                "http://aws-host.com",
                "",
                "/acontext");
        assertThat(fullyQualifiedUrl, is("http://aws-host.com/acontext/abc"));

        // Without port -https
        fullyQualifiedUrl = getFullyQualifiedUrl("/abc",
                "https://aws-host.com",
                "",
                "/acontext");
        assertThat(fullyQualifiedUrl, is("https://aws-host.com/acontext/abc"));

        // Without context
        fullyQualifiedUrl = getFullyQualifiedUrl("/abc",
                "https://aws-host.com",
                "",
                "");
        assertThat(fullyQualifiedUrl, is("https://aws-host.com/abc"));

    }
}