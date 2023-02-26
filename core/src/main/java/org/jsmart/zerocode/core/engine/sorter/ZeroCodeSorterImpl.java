package org.jsmart.zerocode.core.engine.sorter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jsmart.zerocode.core.domain.Step;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeAssertionsProcessor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class ZeroCodeSorterImpl implements ZeroCodeSorter {

    private static final Logger LOGGER = getLogger(ZeroCodeSorterImpl.class);
    private final ObjectMapper mapper;
    private final ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor;

    @Inject
    public ZeroCodeSorterImpl(ZeroCodeAssertionsProcessor zeroCodeAssertionsProcessor,
                              ObjectMapper mapper) {
        this.zeroCodeAssertionsProcessor = zeroCodeAssertionsProcessor;
        this.mapper = mapper;
    }

    @Override
    public String sortArrayAndReplaceInResponse(Step thisStep, String results, String resolvedScenarioState) {
        String key, order, path;
        // reading key and order fields
        try {
            JsonNode sort = thisStep.getSort();
            Map<String, String> fieldMap = convertToMap(sort.toString());
            key = fieldMap.get("key");
            order = fieldMap.getOrDefault("order", "natural");
            path = fieldMap.get("path");
        } catch (Exception e) {
            LOGGER.error("Unable to read values in sort field");
            throw new RuntimeException("Unable to read values in sort field", e);
        }
        if (Objects.isNull(path)) {
            LOGGER.error("Path is null in sort section hence can't sort the response");
            throw new RuntimeException("Path was not specified in sort");
        }
        //
        String transformedPath = zeroCodeAssertionsProcessor.resolveStringJson(path,
                resolvedScenarioState);
        Object result = getArrayToSort(transformedPath, results);

        if (result instanceof JSONArray) {
            JSONArray arrayToSort = (JSONArray) result;

            // sorting passed array
            JSONArray sortedArray = sortArray(arrayToSort, key, order);
            return replaceArrayWithSorted(results, transformedPath, sortedArray);
        } else {
            throw new RuntimeException("Can't sort not an array");
        }
    }

    private JSONArray sortArray(JSONArray arrayToSort, String key, String order) {
        JSONArray sortedJsonArray = new JSONArray();

        List<Map<String, ?>> jsonValues = new ArrayList<>();
        for (Object o : arrayToSort) {
            jsonValues.add((Map<String, ?>) o);
        }

        jsonValues.sort((a, b) -> {
            Comparable valA;
            Comparable valB;

            try {
                valA = (Comparable) a.get(key);
                valB = (Comparable) b.get(key);
            } catch (Exception e) {
                LOGGER.error("Objects can't be compared" + e);
                throw new RuntimeException("Objects can't be compared", e.getCause());
            }
            return order.equalsIgnoreCase(SortOrder.NATURAL.getValue()) ? valA.compareTo(valB)
                    : -valA.compareTo(valB);
        });
        sortedJsonArray.addAll(jsonValues);
        return sortedJsonArray;
    }

    private Map<String, String> convertToMap(String value) {
        try {
            return mapper.readValue(value, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception ex) {
            LOGGER.error("Field Type conversion exception. \nDetails:" + ex);
            throw new RuntimeException(ex);
        }
    }

    public Object getArrayToSort(String path, String results) {
        return JsonPath.read(results, path);
    }

    public String replaceArrayWithSorted(String results, String path, Object sortedArray) {
        return JsonPath.parse(results).set(path, sortedArray).jsonString();
    }

}
