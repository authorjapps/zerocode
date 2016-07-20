package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO: Move this to Smartutils class
public class HelperJsonUtils {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HelperJsonUtils.class);
    private static ObjectMapper mapper = new ObjectMapperProvider().get();


    public static String getContentAsItIsJson(Object bodyContent) {

        if (null == bodyContent) {
            return null;
        }

        final JsonNode bodyJsonNode;
        try {
            /*
             * The bodyContent is a map, because it was read uisng jayway-jaon-path.
             * So needed to be converted into json string.
             */
            final String bodyContentAsString = mapper.writeValueAsString(bodyContent);
            bodyJsonNode = mapper.readValue(bodyContentAsString, JsonNode.class);

            if (bodyJsonNode.isValueNode()) {
                return bodyJsonNode.asText();
            }

            if (bodyJsonNode.size() == 0) {
                return null;
            }

            return bodyJsonNode.toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static Map<String, Object> readHeadersAsMap(Object headers) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            map = (new ObjectMapperProvider()).get().readValue(headers.toString(), HashMap.class);
        } catch (IOException e) {
            // return empty map if exception occurs
            return map;
        }

        return map;
    }

    public static String createAndReturnAssertionResultJson(int httpResponseCode,
                                                            String resultBodyContent, String locationHref) {
        logger.debug("\n#locationHref: " + locationHref);

        if (StringUtils.isEmpty(resultBodyContent)) {
            resultBodyContent = "{}";
        }
        String locationField = locationHref != null ? "	\"Location\" : \"" + locationHref + "\",\n" : "";
        String assertJson = "{\n" +
                "	\"status\" : " + httpResponseCode + ",\n" +
                locationField +
                "	\"body\" : " + resultBodyContent + "\n" +
                " }";

        String formattedStr = SmartUtils.prettyPrintJson(assertJson);

        return formattedStr;
    }

    private void setRequestHeaders(Object headers, ClientRequest clientExecutor) {
        Map<String, Object> headersMap = HelperJsonUtils.readHeadersAsMap(headers);
        for (Object key : headersMap.keySet()) {
            clientExecutor.header((String) key, headersMap.get(key));
        }
    }

    public static String javaObjectAsString(Object value) {

        try {
            ObjectMapper ow = new ObjectMapperProvider().get();
            return ow.writeValueAsString(value);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception while converting IPT Java Object to JsonString" + e);
        }
    }
}
