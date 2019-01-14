package org.jsmart.zerocode.core.engine;

import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class ConfigTest {

    @Test
    public void testConfig_classpathloader() throws IOException {

        final Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("github_host_test.properties"));

        properties.keySet().stream().forEach(thisKey -> {
            assertThat(properties.get(thisKey), notNullValue());
        });
    }


    /*  Working code - Unignore this when switch to Apache2
        @Test
        public void testConfig_viaApache() throws ConfigurationException {
            Configurations configs = new Configurations();
            final PropertiesConfiguration properties = configs.properties("github_host_test.properties");
            System.out.println(">>>> " + properties.getString("org.slf4j.simpleLogger.defaultLogLevel"));
        }
    */

}
