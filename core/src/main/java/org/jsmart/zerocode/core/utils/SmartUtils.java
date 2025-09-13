package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.text.StringSubstitutor;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;

@Singleton
public class SmartUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartUtils.class);

    @Inject
    private ObjectMapper mapper; //<--- remember the static methods can not use this objectMapper. So make the methods non-static if you want to use this objectMapper.

    @Inject
    @Named("YamlMapper")
    private ObjectMapper yamlMapper;

    public <T> String getItRight() throws IOException {
        String jsonAsString = mapper.toString();
        return jsonAsString;
    }

    public <T> String getJsonDocumentAsString(String fileName) throws IOException {
        String jsonAsString = Resources.toString(getClass().getClassLoader().getResource(fileName), UTF_8);
        return jsonAsString;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding == null ? UTF_8 : encoding);
    }

    public static String readJsonAsString(String scenarioFile) {
        try {
            scenarioFile = replaceHome(scenarioFile);
            if (isValidAbsolutePath(scenarioFile)) {
                return readFile(scenarioFile, UTF_8);
            }
            return Resources.toString(Resources.getResource(scenarioFile), defaultCharset());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while parsing the 'Test Scenario' file:{}. " +
                    "Check if it is present n in correct format" + scenarioFile);
            throw new RuntimeException("Exception occurred while reading the 'Test Scenario' file - " + scenarioFile);
        }
    }

    public static String readYamlAsString(String yamlFile) {
        return readJsonAsString(yamlFile);
    }

    public Map<String, Object> readJsonStringAsMap(String json) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        return map;
    }

    public static String sanitizeReportFileName(String fileName) {
    	return fileName.replaceAll("[^A-Za-z0-9 \\-_.]", "_");
    }

    public static List<String> getAllEndPointFiles(String packageName) {
        if(isValidAbsolutePath(packageName)){
            return retrieveScenariosByAbsPath(packageName);
        }
        ClassPathFactory factory = new ClassPathFactory();
        ClassPath jvmClassPath = factory.createFromJVM();
        String[] allSimulationFiles = jvmClassPath.findResources(packageName, new RegExpResourceFilter(".*", ".*\\.json$"));
        if (null == allSimulationFiles || allSimulationFiles.length == 0) {
            LOGGER.error("Test folder is empty or not correctly setup.");
            throw new RuntimeException("NothingFoundHereException: Check the (" + packageName + ") integration test repo folder(empty?). ");
        }

        return Arrays.asList(allSimulationFiles);
    }

    public static List<String> retrieveScenariosByAbsPath(String packageName) {
        packageName = replaceHome(packageName);
        try {
            return Files.walk(Paths.get(packageName))
                    .filter(Files::isRegularFile)
                    .map(aPath -> aPath.toString())
                    .collect(Collectors.toList());
        } catch (IOException exx) {
            LOGGER.error("Exception during reading absolute suite folder - " + exx);
            throw new RuntimeException("Exception during reading absolute suite folder - " + exx);
        }
    }


    public <T> T scenarioFileToJava(String scenarioFile, Class<T> clazz) throws IOException {
        if (scenarioFile.endsWith(".yml") || scenarioFile.endsWith(".yaml")) {
            return yamlMapper.readValue(readYamlAsString(scenarioFile), clazz);
        }

        return mapper.readValue(readJsonAsString(scenarioFile), clazz);
    }

    public static boolean isValidAbsolutePath(String path) {
        path = replaceHome(path);
        Path actualPath = Paths.get(path).toAbsolutePath();
        File file = actualPath.toFile();
        if (file.isAbsolute() && file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static String replaceHome(String path) {
        path = path.replaceFirst("^~", System.getProperty("user.home"));
        return path;
    }

    public List<ScenarioSpec> getScenarioSpecListByPackage(String packageName) {
        List<String> allEndPointFiles = getAllEndPointFiles(packageName);
        List<ScenarioSpec> scenarioSpecList = allEndPointFiles.stream()
                .map(testResource -> {
                    try {
                        return scenarioFileToJava(testResource, ScenarioSpec.class);
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

    public static String resolveToken(String stringWithToken, Map<String, String> paramMap) {
        StringSubstitutor sub = new StringSubstitutor(paramMap);
        return sub.replace(stringWithToken);
    }

    public static String getEnvPropertyValue(String envPropertyKey) {

        final String propertyValue = System.getProperty(envPropertyKey);

        if (propertyValue != null) {
            return propertyValue;
        } else {
            return System.getenv(envPropertyKey);
        }
    }

    /**
     * thisStep : Currently executing step
     * tokenString : JSON_PAYLAOD_FILE or JSON_CONTENT
     * if there is a match for the token, then the json traversal will happen
     */
    public static boolean checkDigNeeded(ObjectMapper mapper, Step thisStep, String tokenString) throws JsonProcessingException {
        String stepJson = mapper.writeValueAsString(thisStep);
        List<String> allTokens = getTestCaseTokens(stepJson);

        return allTokens.toString().contains(tokenString);
    }
    public static boolean checkDigNeeded(ObjectMapper mapper, Step thisStep, String tokenString, String alternateTokenString) throws JsonProcessingException {
        String stepJson = mapper.writeValueAsString(thisStep);
        List<String> allTokens = getTestCaseTokens(stepJson);

        return allTokens.toString().contains(tokenString) || allTokens.toString().contains(alternateTokenString);
    }

    /**
     * Retrieves the first token from the given value string that matches the format "${token}".
     * Ph = Placeholder (e.g. ${JSON.FILE:unit_test_files/filebody_unit_test/common/common_content.json} )
     *
     * @param valueString The string from which to extract the jsonfile path
     * @return The extracted token, or null if no token is found
     */
    public static String getJsonFilePhToken(String valueString) {
        if (valueString != null) {
            List<String> allTokens = getTestCaseTokens(valueString);
            if (allTokens != null && !allTokens.isEmpty()) {
                return allTokens.get(0);
            }
        }
        return null;
    }

}
