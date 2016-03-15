package org.jsmart.smarttester.core.utils;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class SmartUtils {

    public static <T> String getJsonDocumentAsString(String name, T clazz) throws IOException {
        String jsonAsString = Resources.toString(clazz.getClass().getClassLoader().getResource(name), StandardCharsets.UTF_8);
        return jsonAsString;
    }

    public static List<String> getAllEndPointFiles(String packageName) {
        ClassPathFactory factory = new ClassPathFactory();
        ClassPath jvmClassPath = factory.createFromJVM();
        String[] allSimulationFiles = jvmClassPath.findResources(packageName, new RegExpResourceFilter(".*", ".*\\.json$"));
        if(null == allSimulationFiles || allSimulationFiles.length == 0) {
            throw new RuntimeException("YouTriedToSimulateNothingException: Check the (" + packageName + ") integration test repo folder(empty?). " );
        }

        return Arrays.asList(allSimulationFiles);
    }
}
