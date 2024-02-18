package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.univocity.parsers.csv.CsvParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.jsmart.zerocode.core.constants.ZerocodeConstants.DSL_FORMAT;
import static org.jsmart.zerocode.core.di.provider.CsvParserProvider.LINE_SEPARATOR;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Parameterized Tests Steps
 *
 * Processes the Step for each line in the parameterized/parameterizedCsv section.
 *
 * Parameters can be
 * "parameterized": [
 * 200,
 * "Hello",
 * true
 * ]
 *
 * -or-
 *
 * "parameterizedCsv": [
 * "1,    2,   200",
 * "11,  22, 400",
 * "21,  31, 500"
 * ]
 *
 * In each the above cases, the step will execute 3 times.
 *
 * For "parameterized" case, ${0} will resolve to 200, "Hello", true respectively for each run.
 *
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
    public ZeroCodeParameterizedProcessorImpl(ObjectMapper objectMapper, CsvParser csvParser) {
        this.objectMapper = objectMapper;
        this.csvParser = csvParser;
    }

    @Override
    public ScenarioSpec resolveParameterized(ScenarioSpec scenario, int iteration) {

        if (scenario.getParameterized() == null) {

            return scenario;

        } else if (scenario.getParameterized().getValueSource() != null) {

            return resolveParamsValues(scenario, iteration);

        } else if (CollectionUtils.isNotEmpty(scenario.getParameterized().getCsvSource())) {

            return resolveParamsCsv(scenario, iteration);

        }

        throw new RuntimeException("Scenario spec was invalid. Please check the DSL format \ne.g. \n" + DSL_FORMAT);
    }

    private ScenarioSpec resolveParamsValues(ScenarioSpec scenario, int paramIndex) {
        LOGGER.debug("Resolving parameter value-source for index - {}", paramIndex);

        try {
            String stepJson = objectMapper.writeValueAsString(scenario);
            List<Object> parameterized = scenario.getParameterized().getValueSource();

            if (parameterized == null || parameterized.isEmpty()) {
                return scenario;
            }

            Map<String, Object> valuesMap = new HashMap<>();
            valuesMap.put(VALUE_SOURCE_KEY, parameterized.get(paramIndex));

            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            return objectMapper.readValue(resultantStepJson, ScenarioSpec.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterized values for a scenario - " + exx);
        }
    }

    private ScenarioSpec resolveParamsCsv(ScenarioSpec scenario, int paramIndex) {
        LOGGER.debug("Resolving parameter CSV-source for row number - {}", paramIndex);
        try {
            String stepJson = objectMapper.writeValueAsString(scenario);
            List<String> parameterizedCsvList = scenario.getParameterized().getCsvSource();

            if (parameterizedCsvList == null || parameterizedCsvList.isEmpty()) {
                return scenario;
            }

            Map<String, Object> valuesMap = new HashMap<>();
            String csvLine = parameterizedCsvList.get(paramIndex);

            resolveCsvLine(valuesMap, csvLine);

            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            return objectMapper.readValue(resultantStepJson, ScenarioSpec.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterizedCsv values - " + exx);
        }
    }

    private void resolveCsvLine(Map<String, Object> valuesMap, String csvLine) {
        String[] parsedLine = csvParser.parseLine(csvLine + LINE_SEPARATOR);
        AtomicLong index = new AtomicLong(0);
        Arrays.stream(parsedLine)
                .forEach(thisValue -> valuesMap.put(index.getAndIncrement() + "", thisValue));
    }

    private String replaceWithValues(String stepJson, Map<String, Object> valuesMap) {
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(stepJson);
    }

}
