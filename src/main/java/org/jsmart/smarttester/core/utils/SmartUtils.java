package org.jsmart.smarttester.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.jsmart.smarttester.core.domain.FlowSpec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
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

    public Map<String, Object> readJsonStringAsMap(String json) throws IOException {
        java.util.Map<java.lang.String, java.lang.Object> map = new HashMap<>();
        map = mapper.readValue(json, new TypeReference<Map<String, java.lang.Object>>(){});

        return map;
    }

    public static List<String> getAllEndPointFiles(String packageName) {
        ClassPathFactory factory = new ClassPathFactory();
        ClassPath jvmClassPath = factory.createFromJVM();
        String[] allSimulationFiles = jvmClassPath.findResources(packageName, new RegExpResourceFilter(".*", ".*\\.json$"));
        if (null == allSimulationFiles || allSimulationFiles.length == 0) {
            throw new RuntimeException("OverSmartTryingToNothingException: Check the (" + packageName + ") integration test repo folder(empty?). ");
        }

        return Arrays.asList(allSimulationFiles);
    }


    public <T> T jsonFileToJava(String jsonFileName, Class<T> clazz) throws IOException {
        return mapper.readValue(getJsonDocumentAsString(jsonFileName), clazz);
    }

    public List<FlowSpec> getFlowSpecListByPackage(String packageName) {
        List<String> allEndPointFiles = getAllEndPointFiles(packageName);
        List<FlowSpec> flowSpecList = allEndPointFiles.stream()
                .map(flowSpecFile -> {
                    try {
                        return jsonFileToJava(flowSpecFile, FlowSpec.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Exception while deserializing to Spec. Details: " + e);
                    }
                })
                .collect(Collectors.toList());

        return flowSpecList;
    }

    public void checkDuplicateNames(String testPackageName) {
        Set<String> oops = new HashSet<>();

        getFlowSpecListByPackage(testPackageName).stream()
                .forEach(flowSpec -> {
                    if (!oops.add(flowSpec.getFlowName())) {
                        throw new RuntimeException("Oops! Can not run with multiple flow with same name. Found duplicate: " + flowSpec.getFlowName());
                    }

                    /**
                     * Add this if project needs to avoid duplicate step names
                     */
                    /*Set<String> oops = new HashSet<>();
                    flowSpec.getSteps()
                            .forEach(step -> {
                                if(!oops.add(step.getName())){
                                    throw new RuntimeException("Oops! Avoid same step names. Duplicate found: " + step.getName());
                                }
                            });*/
                });
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
