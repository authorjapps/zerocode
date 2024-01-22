package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.slf4j.LoggerFactory;

public class FieldTypeConversionUtils {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FieldTypeConversionUtils.class);

    public static final String INT = "(int)";
    public static final String LONG = "(long)";
    public static final String FLOAT = "(float)";
    public static final String BOOLEAN = "(boolean)";
    public static final String DECIMAL = "(decimal)";

    public static List<String> fieldTypes = Arrays.asList(INT, FLOAT, BOOLEAN, DECIMAL, LONG);

    static Function<String, Integer> integerFunction = (input) ->
            Integer.valueOf(input.substring(INT.length()));

    static Function<String, Long> longFunction = input ->
            Long.valueOf(input.substring(LONG.length()));

    static Function<String, Float> floatFunction = (input) ->
            Float.valueOf(input.substring(FLOAT.length()));

    static Function<String, Float> decimalFunction = (input) ->
            Float.valueOf(input.substring(FLOAT.length()));

    static Function<String, Boolean> booleanFUnction = (input) ->
            Boolean.valueOf(input.substring(BOOLEAN.length()));

    static Map<String, Function> typeMap = new HashMap<String, Function>() {
        {
            put(INT, integerFunction);
            put(LONG, longFunction);
            put(FLOAT, floatFunction);
            put(DECIMAL, decimalFunction);
            put(BOOLEAN, booleanFUnction);
        }
    };

    public static void deepTypeCast(Map<String, Object> map) {

        map.entrySet().stream().forEach(entry -> {

            Object value = entry.getValue();

            if (value instanceof Map) {
                deepTypeCast((Map<String, Object>) value);

            } else if (value instanceof ArrayList) {
                for(int index = 0; index < ((ArrayList<?>) value).size(); index++){
                    Object thisItemValue = ((ArrayList<?>) value).get(index);
                    if (thisItemValue instanceof Map) {
                        deepTypeCast((Map<String, Object>) thisItemValue);
                    } else{
                        replacePrimitiveValues(((ArrayList) value), index, thisItemValue);
                    }
                }
            } else {
                LOGGER.debug("Leaf node found = {}, checking for type value...", value);
                replaceNodeValue(entry, value);
            }
        });
    }


    private static void replaceNodeValue(Map.Entry<String, Object> entry, Object thisItem) {
        try {
            if (thisItem != null) {
                fieldTypes.stream().forEach(currentType -> {
                    if (thisItem.toString().startsWith(currentType)) {
                        entry.setValue((typeMap.get(currentType)).apply(thisItem.toString()));
                    }
                });
            }
        } catch (Exception exx) {
            String errorMsg = "Can not convert '" + entry.getValue() + "'.";
            LOGGER.error("{} with exception details: {}",  errorMsg, exx);
            throw new RuntimeException(errorMsg + exx);
        }
    }

    private static void replacePrimitiveValues(ArrayList<Object> valueList, Integer index, Object thisItem) {
        try {
            if (thisItem != null) {
                fieldTypes.stream().forEach(currentType -> {
                    if (thisItem.toString().startsWith(currentType)) {
                        valueList.set(index, (typeMap.get(currentType)).apply(thisItem.toString()));
                    }
                });
            }
        } catch (Exception exx) {
            String errorMsg = "Can not convert '" + thisItem + "'.";
            LOGGER.error(errorMsg + "\nException Details:" + exx);
            throw new RuntimeException(errorMsg + exx);
        }
    }
}
