package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsmart.zerocode.core.domain.Step;
import org.slf4j.Logger;

import static org.jsmart.zerocode.core.engine.tokens.ZeroCodeValueTokens.JSON_PAYLOAD_FILE;
import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;
import static org.jsmart.zerocode.core.utils.TokenUtils.getTestCaseTokens;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <h3>External File Resolver</h3>
 * <p>
 * Processes the Step definition and resolves any reference to the external file.
 * <p>
 * Given a Json Java map, it digs deep into the fields and finds the references to the external content
 * in the classpath and replaces the value of this key with the content from the file.
 *
 * Issue-2 - Feature requested by @UmeshIghe(Github)
 * Github Link - https://github.com/authorjapps/zerocode/issues/2
 *
 * Suggestion and Recommendation:
 * Try to keep the test cases as independent possible. Do not create too much nested dependencies which adds
 * unnecessary complexity to the project. The complexity could be in terms project maintenance or interpreting the
 * test case itself. We should use the IDE features which now a days makes a lot easier in terms of dealing with
 * JSON contents to keep the testing life cycle simple.
 *
 */
@Singleton
public class ZeroCodeExternalFileProcessorImpl implements ZeroCodeExternalFileProcessor {
    private static final Logger LOGGER = getLogger(ZeroCodeExternalFileProcessorImpl.class);

    private final ObjectMapper objectMapper;

    @Inject
    public ZeroCodeExternalFileProcessorImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Resolves the external file content for a place holder against a key e.g. in the payload or assertions.
     *
     * First the logic checks if dig-deep needed to avoid unwanted recursions. If not needed, the step definition is
     * returned intact. Otherwise calls the dig deep method to perform the operation.
     *
     * @param thisStep A step, defining the API call and validation
     * @return The effective step definition
     */
    @Override
    public Step resolveExtJsonFile(Step thisStep) {

        try {

            if (!checkDigNeeded(thisStep)) {
                return thisStep;
            }

            JsonNode stepNode = objectMapper.convertValue(thisStep, JsonNode.class);

            Map<String, Object> stepMap = objectMapper.readValue(stepNode.toString(), new TypeReference<Map<String, Object>>() {
            });

            digReplaceContent(stepMap);

            JsonNode jsonStepNode = objectMapper.valueToTree(stepMap);

            return objectMapper.treeToValue(jsonStepNode, Step.class);

        } catch (Exception exx) {

            LOGGER.error("External file reading exception - {}", exx.getMessage());

            throw new RuntimeException("External file reading exception. Details - " + exx);

        }

    }

    @Override
    public List<Step> createFromStepFile(Step thisStep, String stepId) {
        List<Step> thisSteps = new ArrayList<>();
        if (thisStep.getStepFile() != null) {
            try {
                thisSteps.add(objectMapper.treeToValue(thisStep.getStepFile(), Step.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("\n### Error while parsing for stepId - {}, stepFile - {}",
                        stepId, thisStep.getStepFile());
                throw new RuntimeException(e);
            }
        } else if(null != thisStep.getStepFiles() && !thisStep.getStepFiles().isEmpty()) {
            try {
                for(int i = 0; i < thisStep.getStepFiles().size(); i++)
                    thisSteps.add(objectMapper.treeToValue(thisStep.getStepFiles().get(i), Step.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("\n### Error while parsing for stepId - {}, stepFile - {}",
                        stepId, thisStep.getStepFiles());
                throw new RuntimeException(e);
            }
        }
        return thisSteps;
    }

    /**
     * Digs deep into the nested map and looks for external file reference,if found, replaces the place holder with
     * the file content. This is handy when the engineers wants to drive the common contents from a central place.
     *
     * @param map A map representing the key-value pairs, can be nested
     */
    void digReplaceContent(Map<String, Object> map) {

        map.entrySet().stream().forEach(entry -> {

            Object value = entry.getValue();

            if (value instanceof Map) {
                digReplaceContent((Map<String, Object>) value);

            } else {
                LOGGER.debug("Leaf node found = {}, checking for any external json file...", value);
                if (value != null && value.toString().contains(JSON_PAYLOAD_FILE)) {
                    LOGGER.info("Found external JSON file place holder = {}. Replacing with content", value);
                    String valueString = value.toString();
                    String token = getJsonFilePhToken(valueString);
                    if (token != null && token.startsWith(JSON_PAYLOAD_FILE)) {
                        String resourceJsonFile = token.substring(JSON_PAYLOAD_FILE.length());
                        try {
                            Object jsonFileContent = objectMapper.readTree(readJsonAsString(resourceJsonFile));
                            entry.setValue(jsonFileContent);
                        } catch (Exception exx) {
                            LOGGER.error("External file reference exception - {}", exx.getMessage());
                            throw new RuntimeException(exx);
                        }
                    }
                    // ----------------------------------------------------
                    // Extension- for XML file type in case a ticket raised
                    // ----------------------------------------------------
                    /*
                    else if (token != null && token.startsWith(XML_FILE)) {

                    }
                    */

                    // ---------------------------------------------------
                    // Extension- for Other types in case a ticket raised
                    // ---------------------------------------------------
                    /*
                    else if (token != null && token.startsWith(OTHER_FILE)) {

                    }
                    */

                }
            }
        });
    }

    private String getJsonFilePhToken(String valueString) {
        if (valueString != null) {
            List<String> allTokens = getTestCaseTokens(valueString);
            if (allTokens != null && !allTokens.isEmpty()) {
                return allTokens.get(0);
            }
        }
        return null;
    }

    boolean checkDigNeeded(Step thisStep) throws JsonProcessingException {
        String stepJson = objectMapper.writeValueAsString(thisStep);
        List<String> allTokens = getTestCaseTokens(stepJson);

        return allTokens.toString().contains(JSON_PAYLOAD_FILE);
    }

}
