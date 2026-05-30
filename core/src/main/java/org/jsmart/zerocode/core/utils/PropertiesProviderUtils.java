package org.jsmart.zerocode.core.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.ZEROCODE_PROPERTIES_FILE;
import static org.jsmart.zerocode.core.di.PropertyKeys.RESTFUL_APPLICATION_ENDPOINT_CONTEXT;
import static org.jsmart.zerocode.core.di.PropertyKeys.RESTFUL_APPLICATION_ENDPOINT_HOST;
import static org.jsmart.zerocode.core.di.PropertyKeys.RESTFUL_APPLICATION_ENDPOINT_PORT;
import static org.jsmart.zerocode.core.di.PropertyKeys.WEB_APPLICATION_ENDPOINT_CONTEXT;
import static org.jsmart.zerocode.core.di.PropertyKeys.WEB_APPLICATION_ENDPOINT_HOST;
import static org.jsmart.zerocode.core.di.PropertyKeys.WEB_APPLICATION_ENDPOINT_PORT;
import static org.jsmart.zerocode.core.utils.SmartUtils.replaceHome;


public class PropertiesProviderUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesProviderUtils.class);

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


    public static Properties loadAbsoluteProperties(String host, Properties properties) {
        try {
            host = replaceHome(host);

            InputStream inputStream = new FileInputStream(host);
            properties.load(inputStream);

            checkAndLoadOldProperties(properties);

            return properties;

        } catch (Exception exx) {
            throw new RuntimeException(exx);
        }
    }

    public static Properties loadCustomZerocodeProperties() {
        Properties props = new Properties();
        InputStream inputStream = PropertiesProviderUtils.class
                .getClassLoader()
                .getResourceAsStream(ZEROCODE_PROPERTIES_FILE);
        if (inputStream == null) {
            return props;
        }
        try {
            props.load(inputStream);
        } catch (IOException e) {
            LOGGER.debug("Could not load {}. Using defaults. Details: {}", ZEROCODE_PROPERTIES_FILE, e.getMessage());
        }
        return props;
    }

    public static void checkAndLoadOldProperties(Properties properties) {

        if (properties.get(WEB_APPLICATION_ENDPOINT_HOST) == null && properties.get(RESTFUL_APPLICATION_ENDPOINT_HOST) != null) {
            Object oldPropertyValue = properties.get(RESTFUL_APPLICATION_ENDPOINT_HOST);
            properties.setProperty(WEB_APPLICATION_ENDPOINT_HOST, oldPropertyValue != null ? oldPropertyValue.toString() : null);
        }

        if (properties.get(WEB_APPLICATION_ENDPOINT_PORT) == null && properties.get(RESTFUL_APPLICATION_ENDPOINT_PORT) != null) {
            Object oldPropertyValue = properties.get(RESTFUL_APPLICATION_ENDPOINT_PORT);
            properties.setProperty(WEB_APPLICATION_ENDPOINT_PORT, oldPropertyValue != null ? oldPropertyValue.toString() : null);
        }

        if (properties.get(WEB_APPLICATION_ENDPOINT_CONTEXT) == null && properties.get(RESTFUL_APPLICATION_ENDPOINT_CONTEXT) != null) {
            Object oldPropertyValue = properties.get(RESTFUL_APPLICATION_ENDPOINT_CONTEXT);
            properties.setProperty(WEB_APPLICATION_ENDPOINT_CONTEXT, oldPropertyValue != null ? oldPropertyValue.toString() : null);
        }

    }
}
