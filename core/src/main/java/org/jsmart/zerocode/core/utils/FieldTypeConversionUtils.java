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
    private static ObjectMapper mapper = new ObjectMapperProvider().get();

    public static final String INT = "(int)";
    public static final String FLOAT = "(float)";
    public static final String BOOLEAN = "(boolean)";
    public static final String DECIMAL = "(decimal)";

    public static List<String> fieldTypes = Arrays.asList(INT, FLOAT, BOOLEAN, DECIMAL);

    static Function<String, Integer> integerFunction = (input) ->
            Integer.valueOf(input.substring(INT.length()));

    static Function<String, Float> floatFunction = (input) ->
            Float.valueOf(input.substring(FLOAT.length()));

    static Function<String, Float> decimalFunction = (input) ->
            Float.valueOf(input.substring(FLOAT.length()));

    static Function<String, Boolean> booleanFUnction = (input) ->
            Boolean.valueOf(input.substring(BOOLEAN.length()));

    static Map<String, Function> typeMap = new HashMap<String, Function>(){
        {
            put(INT, integerFunction);
            put(FLOAT, floatFunction);
            put(DECIMAL, decimalFunction);
            put(BOOLEAN, booleanFUnction);
        }
    };

    public static void digTypeCast(Map<String, Object> map) {

        map.entrySet().stream().forEach(entry -> {

            Object value = entry.getValue();

            if (value instanceof Map) {
                digTypeCast((Map<String, Object>) value);

            } else if(value instanceof ArrayList){
                ((ArrayList)value).forEach(thisItem -> {
                    if (thisItem instanceof Map) {
                        digTypeCast((Map<String, Object>) thisItem);
                    }
                    LOGGER.debug("ARRAY - Leaf node found = {}, checking for type value...", thisItem);
                    replaceNodeValue(entry, thisItem);
                });
            }
            else {
                LOGGER.debug("Leaf node found = {}, checking for type value...", value);
                replaceNodeValue(entry, value);
            }
        });
    }


    private static void replaceNodeValue(Map.Entry<String, Object> entry, Object thisItem) {
        try{
            if (thisItem != null && thisItem.toString().startsWith(INT)) {
                entry.setValue((typeMap.get(INT)).apply(thisItem.toString()));
            }
            else if (thisItem != null && thisItem.toString().startsWith(DECIMAL)) {
                entry.setValue((typeMap.get(DECIMAL)).apply(thisItem.toString()));
            }
            else if (thisItem != null && thisItem.toString().startsWith(FLOAT)) {
                entry.setValue((typeMap.get(FLOAT)).apply(thisItem.toString()));
            }
            else if (thisItem != null && thisItem.toString().startsWith(BOOLEAN)) {
                entry.setValue((typeMap.get(BOOLEAN)).apply(thisItem.toString()));
            }
        } catch(Exception exx){
            String errorMsg = "Can not convert '" + entry.getValue() + "'.";
            LOGGER.error(errorMsg + "\nException Details:" + exx);
            throw new RuntimeException(errorMsg + exx);
        }

    }
}
