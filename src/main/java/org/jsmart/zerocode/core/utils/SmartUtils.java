package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.Charset.defaultCharset;

@Singleton
public class SmartUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartUtils.class);
    
    @Inject
    private ObjectMapper mapper; //<--- remember the static methods can not use this objectMapper. So make the methods non-static if you want to use this objectMapper.

    public <T> String getItRight() throws IOException {
        String jsonAsString = mapper.toString();
        return jsonAsString;
    }

    public <T> String getJsonDocumentAsString(String fileName) throws IOException {
        String jsonAsString = Resources.toString(getClass().getClassLoader().getResource(fileName), StandardCharsets.UTF_8);
        return jsonAsString;
    }

    public static String readJsonAsString(String jsonFileName) throws IOException {
        return Resources.toString(Resources.getResource(jsonFileName), defaultCharset());
    }


    public Map<String, Object> readJsonStringAsMap(String json) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

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
        return mapper.readValue(readJsonAsString(jsonFileName), clazz);
    }

    public List<ScenarioSpec> getScenarioSpecListByPackage(String packageName) {
        List<String> allEndPointFiles = getAllEndPointFiles(packageName);
        List<ScenarioSpec> scenarioSpecList = allEndPointFiles.stream()
                .map(flowSpecFile -> {
                    try {
                        return jsonFileToJava(flowSpecFile, ScenarioSpec.class);
                    } catch (IOException e) {
                        throw new RuntimeException("Exception while deserializing to Spec. Details: " + e);
                    }
                })
                .collect(Collectors.toList());

        return scenarioSpecList;
    }

    public void checkDuplicateScenarios(String testPackageName) {
        Set<String> oops = new HashSet<>();

        getScenarioSpecListByPackage(testPackageName).stream()
                .forEach(scenarioSpec -> {
                    if (!oops.add(scenarioSpec.getScenarioName())) {
                        throw new RuntimeException("Oops! Can not run with multiple Scenarios with same name. Found duplicate: " + scenarioSpec.getScenarioName());
                    }

                    /**
                     * Add this if project needs to avoid duplicate step names
                     */
                    /*Set<String> oops = new HashSet<>();
                    scenarioSpec.getSteps()
                            .forEach(step -> {
                                if(!oops.add(step.getName())){
                                    throw new RuntimeException("Oops! Avoid same step names. Duplicate found: " + step.getName());
                                }
                            });*/
                });
    }

    public static String prettyPrintJson(String jsonString) {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        try {
            final JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

        } catch (IOException e) {
            /*
             *  Pretty-print logic threw an exception, not a big deal, print the original json then.
             */
            LOGGER.error("Non-JSON content was encountered. So pretty print did not format it and returned the raw text");
            return jsonString;
        }

    }

    public static String prettyPrintJson(JsonNode jsonNode) {
        String indented = jsonNode.toString();

        final ObjectMapper objectMapper = new ObjectMapperProvider().get();

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

        } catch (IOException e) {
            /*
             *  Pretty-print logic threw an exception, not a big deal, print the original json then.
             */
            return indented;
        }
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * This method was introduced later,
     * But Framework uses- ZeroCodeJsonTestProcesorImpl#getAllTokens(java.lang.String)
     *
     * @param aString - Containing the pattern
     * @return List of strings matching the pattern
     */
    public static List<String> getAllTokens(String aString) {

        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(aString);

        List<String> keyTokens = new ArrayList<>();

        while (matcher.find()) {
            keyTokens.add(matcher.group(1));
        }

        return keyTokens;
    }

    public static String resolveToken(String stringWithToken, Map<String, String> paramMap) {
        StrSubstitutor sub = new StrSubstitutor(paramMap);
        return sub.replace(stringWithToken);
    }

    public static String getEnvPropertyValue(String envPropertyKey){

        final String propertyValue = System.getProperty(envPropertyKey);

        if (propertyValue != null) {
            return propertyValue;
        } else {
            return System.getenv(envPropertyKey);
        }
    }
}
