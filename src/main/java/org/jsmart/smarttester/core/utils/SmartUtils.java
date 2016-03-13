package org.jsmart.smarttester.core.utils;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SmartUtils {

    public static <T> String getJsonDocumentAsString(String name, T clazz) throws IOException {
        String jsonAsString = Resources.toString(clazz.getClass().getClassLoader().getResource(name), StandardCharsets.UTF_8);
        return jsonAsString;
    }
}
