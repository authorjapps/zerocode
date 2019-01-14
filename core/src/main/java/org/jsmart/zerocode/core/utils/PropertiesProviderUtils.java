package org.jsmart.zerocode.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesProviderUtils {


    private static Properties properties = new Properties();

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static Properties getProperties(String propertyResourceFile) {
        InputStream inputStream = PropertiesProviderUtils.class
                .getClassLoader()
                .getResourceAsStream(propertyResourceFile);

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}
