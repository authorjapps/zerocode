package org.jsmart.zerocode.core.utils;

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

public class EnvUtils {

    public static Integer getEnvValueInt(String envKey) {
        String envValue = getProperty(envKey) == null ? getenv(envKey) : getProperty(envKey);
        return envValue == null ? null : parseInt(envValue);
    }

    public static String getEnvValueString(String envKey) {
        return getProperty(envKey) == null ? getenv(envKey) : getProperty(envKey);
    }

}
