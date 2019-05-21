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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.univocity.parsers.csv.CsvParser;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.domain.Step;
import org.slf4j.Logger;

import static org.jsmart.zerocode.core.di.provider.CsvParserProvider.LINE_SEPARATOR;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <h3>Parameterized Tests Steps</h3>
 * <p>
 * Processes the Step for each line in the parameterized/parameterizedCsv section.
 * <p>
 * <p>
 * Parameters can be
 * "parameterized": [
 * 200,
 * "Hello",
 * true
 * ]
 * <p>
 * -or-
 * <p>
 * "parameterizedCsv": [
 * "1,    2,   200",
 * "11,  22, 400",
 * "21,  31, 500"
 * ]
 * <p>
 * In each the above cases, the step will execute 3 times.
 * <p>
 * For "parameterized" case ${0} will resolve to 200, "Hello", true respectively for each run.
 * <p>
 * For "parameterizedCsv" case, ${0}, ${1}, ${2} will resolve to "1", "2", "200" for the first run.
 * Then it will resolve to "11",  "22", "400" for the 2nd run ans so on.
 */
@Singleton
public class ZeroCodeParameterizedProcessorImpl implements ZeroCodeParameterizedProcessor {
    private static final Logger LOGGER = getLogger(ZeroCodeParameterizedProcessorImpl.class);
    public static final String VALUE_SOURCE_KEY = "0";

    private final ObjectMapper objectMapper;

    private final CsvParser csvParser;

    @Inject
    public ZeroCodeParameterizedProcessorImpl(ObjectMapper objectMapper, CsvParser csvParser, CsvParser csvParser1) {
        this.objectMapper = objectMapper;
        this.csvParser = csvParser1;
    }

    @Override
    public Step processParameterized(Step thisStep, int i) {
        Step parameterizedStep;
        if (thisStep.getParameterized() != null) {

            parameterizedStep = resolveParamsValues(thisStep, i);

        } else if (thisStep.getParameterizedCsv() != null) {

            parameterizedStep = resolveParamsCsv(thisStep, i);

        } else {

            parameterizedStep = thisStep;

        }
        return parameterizedStep;
    }

    private Step resolveParamsValues(Step step, int paramIndex) {
        try {
            String stepJson = objectMapper.writeValueAsString(step);
            List<Object> parameterized = step.getParameterized();

            if (parameterized == null || parameterized.isEmpty()) {
                return step;
            }

            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put(VALUE_SOURCE_KEY, parameterized.get(paramIndex));
            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            return objectMapper.readValue(resultantStepJson, Step.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterized values - " + exx);
        }
    }

    private Step resolveParamsCsv(Step step, int paramIndex) {
        try {
            String stepJson = objectMapper.writeValueAsString(step);
            List<String> parameterizedCsvList = step.getParameterizedCsv();

            if (parameterizedCsvList == null || parameterizedCsvList.isEmpty()) {
                return step;
            }

            Map<String, Object> valuesMap = new HashMap<>();
            String csvLine = parameterizedCsvList.get(paramIndex);

            String[] parsedLine = csvParser.parseLine(csvLine + LINE_SEPARATOR);
            AtomicLong index = new AtomicLong(0);
            Arrays.stream(parsedLine)
                    .forEach(thisValue -> valuesMap.put(index.getAndIncrement() + "", thisValue));

            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            return objectMapper.readValue(resultantStepJson, Step.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterizedCsv values - " + exx);
        }
    }

    private String replaceWithValues(String stepJson, Map<String, Object> valuesMap) {
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(stepJson);
    }

}
