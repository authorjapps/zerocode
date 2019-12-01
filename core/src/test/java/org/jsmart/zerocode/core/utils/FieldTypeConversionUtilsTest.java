package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.jsmart.zerocode.core.utils.FieldTypeConversionUtils.digTypeCast;
import static org.junit.Assert.assertEquals;

public class FieldTypeConversionUtilsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    ObjectMapper mapper = new ObjectMapperProvider().get();

    @Test
    public void testSubstituted_v3() throws IOException {
        String originalJson = "{\n" +
                "    \"found\": true,\n" +
                "    \"currentAddress\":{\n" +
                "      \"line1\": \"address line1\",  \n" +
                "      \"line2\": \"address line2\"  \n" +
                "    },\n" +
                "    \"results\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Foo\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2.35,\n" +
                "            \"name\": \"Bar\",\n" +
                "            \"isActive\": false\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        String jsonViaPath = "{\n" +
                "    \"found\": \"(boolean)${$.found}\",\n" +
                "    \"currentAddress\":{\n" +
                "      \"line1\": \"address line1\",\n" +
                "      \"line2\": \"address line2\"\n" +
                "    }," +
                "    \"results\": [\n" +
                "        {\n" +
                "            \"id\": \"(int)${$.results[0].id}\",\n" +
                "            \"name\": \"Foo - ${$.results[0].id} - ${$.found}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"(float)${$.results[1].id}\",\n" +
                "            \"name\": \"Bar - ${$.results[1].id}\",\n" +
                "            \"isActive\": \"(boolean)${$.results[1].isActive}\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        List<String> tokens = new ArrayList<>();
        tokens.add("$.found");
        tokens.add("$.results[0].id");
        tokens.add("$.results[1].id");
        tokens.add("$.results[1].isActive");

        Map<String, Object> paramMap = new HashMap<>();

        tokens.forEach(thisPath -> {
            Object pathValue = JsonPath.read(originalJson, thisPath);
            paramMap.put(thisPath, pathValue);
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);
        String resolvedJson = sub.replace(jsonViaPath);

        Map<String, Object> stepMap = mapper.readValue(resolvedJson, new TypeReference<Map<String, Object>>() {
        });

        digTypeCast(stepMap);

        JsonNode jsonNode = mapper.valueToTree(stepMap);

        assertEquals(true, jsonNode.get("found").asBoolean());
        assertEquals("{\"id\":2.35,\"name\":\"Bar - 2.35\",\"isActive\":false}",
                jsonNode.get("results").get(1).toString());
        assertEquals("address line1", jsonNode.get("currentAddress").get("line1").asText());
    }

    @Test
    public void testSubstituted_incorrectTypeException() throws IOException {
        String originalJson = "{\n" +
                "    \"found\": true,\n" +
                "    \"currentAddress\":{\n" +
                "      \"line1\": \"address line1\",  \n" +
                "      \"line2\": \"address line2\"  \n" +
                "    },\n" +
                "    \"results\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Foo\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2.35,\n" +
                "            \"name\": \"Bar\",\n" +
                "            \"isActive\": false\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        String jsonViaPath = "{\n" +
                "    \"found\": \"(boolean)${$.found}\",\n" +
                "    \"currentAddress\":{\n" +
                "      \"line1\": \"address line1\",\n" +
                "      \"line2\": \"address line2\"\n" +
                "    }," +
                "    \"results\": [\n" +
                "        {\n" +
                "            \"id\": \"(int)${$.results[0].id}\",\n" +
                "            \"name\": \"Foo - ${$.results[0].id} - ${$.found}\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"(float)${$.results[1].id}\",\n" +
                "            \"name\": \"Bar - ${$.results[1].id}\",\n" +
                "            \"isActive\": \"(int)${$.results[1].isActive}\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        List<String> tokens = new ArrayList<>();
        tokens.add("$.found");
        tokens.add("$.results[0].id");
        tokens.add("$.results[1].id");
        tokens.add("$.results[1].isActive");

        Map<String, Object> paramMap = new HashMap<>();

        tokens.forEach(thisPath -> {
            Object pathValue = JsonPath.read(originalJson, thisPath);
            paramMap.put(thisPath, pathValue);
        });

        StrSubstitutor sub = new StrSubstitutor(paramMap);
        String resolvedJson = sub.replace(jsonViaPath);

        Map<String, Object> stepMap = mapper.readValue(resolvedJson, new TypeReference<Map<String, Object>>() {
        });

        expectedException.expectMessage("Can not convert '(int)false");
        expectedException.expect(RuntimeException.class);
        digTypeCast(stepMap);
    }
}