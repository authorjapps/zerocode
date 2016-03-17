package org.jsmart.smarttester.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import org.jsmart.smarttester.core.domain.FlowSpec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SmartUtils {
    @Inject
    private ObjectMapper mapper; //<--- remember the static methods can not use this objectMapper. So make the methods non-static if you want to use this objectMapper.

    public <T> String getItRight() throws IOException {
        String jsonAsString = mapper.toString();
        return jsonAsString;
    }

    public <T> String getJsonDocumentAsString(String name) throws IOException {
        String jsonAsString = Resources.toString(getClass().getClassLoader().getResource(name), StandardCharsets.UTF_8);
        return jsonAsString;
    }

    public static List<String> getAllEndPointFiles(String packageName) {
        ClassPathFactory factory = new ClassPathFactory();
        ClassPath jvmClassPath = factory.createFromJVM();
        String[] allSimulationFiles = jvmClassPath.findResources(packageName, new RegExpResourceFilter(".*", ".*\\.json$"));
        if(null == allSimulationFiles || allSimulationFiles.length == 0) {
            throw new RuntimeException("OverSmartTryingToNothingException: Check the (" + packageName + ") integration test repo folder(empty?). " );
        }

        return Arrays.asList(allSimulationFiles);
    }


    public  <T> T jsonToJava(String jsonFileName, Class<T> clazz) throws IOException {
        return mapper.readValue(getJsonDocumentAsString(jsonFileName), clazz);
    }

    public List<FlowSpec> getFlowSpecListByPackage(String packageName) {
        List<String> allEndPointFiles = getAllEndPointFiles(packageName);
        List<FlowSpec> flowSpecList = allEndPointFiles.stream()
                .map(flowSpecFile -> {
                    FlowSpec spec = null;
                    try {
                        //spec = jsonToFlowSpec(flowSpecFile);
                        spec = jsonToJava(flowSpecFile, FlowSpec.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Exception while deserializing to Spec. Details: " + e);
                    }
                    return spec;
                })
                .collect(Collectors.toList());

        return flowSpecList;
    }
}
