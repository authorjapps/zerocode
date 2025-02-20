package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesReader {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("ERROR: Configuration file '" + CONFIG_FILE + "' not found in resources!");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key, "");  // If this is empty, I will default to empty string being returned.
    }

    public static int getIntProperty(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key, "0")); // If empty, default to 0.
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid number format for key: " + key);
            return 0;
        }
    }
}