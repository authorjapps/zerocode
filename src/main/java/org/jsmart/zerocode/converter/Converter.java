package org.jsmart.zerocode.converter;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface Converter {
    Object xmlToJson(String xmlObject);

    Object jsonToJson(String jsonString) throws IOException;

    Object jsonBlockToJson(JsonNode jsonNode) throws IOException;

    default Object jsonNodeToJson(JsonNode jsonNode) throws IOException {
        return jsonBlockToJson(jsonNode);
    }
}
