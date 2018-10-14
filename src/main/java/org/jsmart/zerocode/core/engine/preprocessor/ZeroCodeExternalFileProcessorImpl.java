/*
 * Copyright (C) 2014 jApps Ltd and
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jsmart.zerocode.core.domain.Step;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesorImpl.JSON_PAYLOAD_FILE;
import static org.jsmart.zerocode.core.utils.SmartUtils.getAllTokens;
import static org.jsmart.zerocode.core.utils.SmartUtils.readJsonAsString;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <h3>External File Resolver</h3>
 * <p>
 * Processes the Step definition and resolves any reference to the external file.
 * <p>
 * Given a Json Java map, it digs deep into the fields and finds the references to the external content
 * in the classpath and replaces the value of this key with the content from the file.
 *
 * @author nirmal.fleet@google.com (Nirmal Chandra)
 */
@Singleton
public class ZeroCodeExternalFileProcessorImpl implements ZeroCodeExternalFileProcessor {
    private static final Logger LOGGER = getLogger(ZeroCodeExternalFileProcessorImpl.class);

    private final ObjectMapper objectMapper;

    @Inject
    public ZeroCodeExternalFileProcessorImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
            List<String> allTokens = getAllTokens(valueString);
            if (allTokens != null && !allTokens.isEmpty()) {
                return allTokens.get(0);
            }
        }
        return null;
    }

    boolean checkDigNeeded(Step thisStep) throws JsonProcessingException {
        String stepJson = objectMapper.writeValueAsString(thisStep);
        List<String> allTokens = getAllTokens(stepJson);

        return allTokens.toString().contains(JSON_PAYLOAD_FILE);
    }

}
