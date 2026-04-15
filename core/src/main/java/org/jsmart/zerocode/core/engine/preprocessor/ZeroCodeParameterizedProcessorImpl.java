package org.jsmart.zerocode.core.engine.preprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jsmart.zerocode.core.di.provider.CsvParserProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.utils.TokenUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final CsvParserProvider csvParser;

    @Inject
    public ZeroCodeParameterizedProcessorImpl(ObjectMapper objectMapper, CsvParserProvider csvParser) {
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

            String[] headers = retrieveCsvHeaders(parameterizedCsvList.get(0),scenario.getParameterized().isWithHeaders());

            paramIndex = headers == null ? paramIndex : paramIndex+1;

            String csvLine = parameterizedCsvList.get(paramIndex);

            Map<String, Object> valuesMap = resolveCsvLine(csvLine, headers);

            String resultantStepJson = replaceWithValues(stepJson, valuesMap);

            ensureAllParamsResolved(resultantStepJson, headers);

            return objectMapper.readValue(resultantStepJson, ScenarioSpec.class);

        } catch (Exception exx) {
            throw new RuntimeException("Error while resolving parameterizedCsv values - " + exx);
        }
    }

    private String[] retrieveCsvHeaders(String csvHeaderLine, Boolean isWithHeaders) {
        String[] parsedHeaderLine = csvParser.parseLine(csvHeaderLine + LINE_SEPARATOR);
        if (isWithHeaders && parsedHeaderLine.length > 0) {
            boolean hasPipes = Arrays.stream(parsedHeaderLine).allMatch(s -> s.matches("^\\|.*\\|$"));
            if (hasPipes) {
                LOGGER.warn("DEPRECATION WARNING: The '||' (piped) syntax in CSV headers is deprecated. " +
                "Please stop using it and start using standard header syntax with 'withHeaders: true' field instead. " +
                "Visit the documentation for more details.");
                return Arrays.stream(parsedHeaderLine).map(s -> s.substring(1,s.length()-1)).toArray(String[]::new);
            }
            return parsedHeaderLine;
        } else {
            return null;
        }
    }

    private Map<String, Object> resolveCsvLine(String csvLine, String[] headers) {
        Map<String, Object> valuesMap = new HashMap<>();
        String[] parsedLine = csvParser.parseLine(csvLine + LINE_SEPARATOR);
        IntStream.range(0, parsedLine.length).forEach(i -> valuesMap.put(i + "", parsedLine[i]));

        if (headers != null){
            IntStream.range(0, headers.length).forEach(i -> {
                if(!headers[i].contains(" ") && !headers[i].isEmpty()){
                    valuesMap.put("PARAM."+headers[i], TokenUtils.resolveKnownTokens(parsedLine[i]).toString());
                }
            });
        }
        return valuesMap;
    }

    private String replaceWithValues(String stepJson, Map<String, Object> valuesMap) {
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        return sub.replace(stepJson);
    }

    private void ensureAllParamsResolved(String resultantStepJson, String[] availableHeaders) {
        Pattern pattern = Pattern.compile("\\$\\{PARAM\\.([^}]+)\\}");
        Matcher matcher = pattern.matcher(resultantStepJson);
        
        if (matcher.find()) {
            String msg;
            if (availableHeaders == null || availableHeaders.length == 0) {
                msg = "No available headers found. Ensure your CSV file is not empty and that '\"withHeaders\": true' is defined in the scenario.";
            } else {
                msg = "Available headers are: [" + String.join(", ", availableHeaders) + "].";
            }
            throw new RuntimeException("Invalid CSV header referenced in scenario. The column '" + matcher.group(1) + "' does not exist. " + msg);
            
        }
    }
}
