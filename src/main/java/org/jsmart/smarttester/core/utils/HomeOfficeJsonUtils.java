package org.jsmart.smarttester.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jsmart.smarttester.core.di.ObjectMapperProvider;
import org.jsmart.smarttester.core.runner.MultiStepsScenarioRunnerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// Move this to Smartutils class
public class HomeOfficeJsonUtils {
    private static final Logger logger = Logger.getLogger(MultiStepsScenarioRunnerImpl.class.getName());
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
        logger.info("\n#locationHref: " + locationHref);

        if (StringUtils.isEmpty(resultBodyContent)) {
            resultBodyContent = "{}";
        }
        String locationField = locationHref != null ? "	\"Location\" : \"" + locationHref + "\",\n" : "";
        String assertJson = "{\n" +
                "	\"status\" : " + httpResponseCode + ",\n" +
                locationField +
                "	\"body\" : " + resultBodyContent + "\n" +
                " }";

        logger.info("\n#bodyJson -> assertJson: " + assertJson);

        String formattedStr = SmartUtils.prettyPrintJson(assertJson);

        return formattedStr;
    }

    private void setRequestHeaders(Object headers, ClientRequest clientExecutor) {
        Map<String, Object> headersMap = HomeOfficeJsonUtils.readHeadersAsMap(headers);
        for (Object key : headersMap.keySet()) {
            clientExecutor.header((String) key, headersMap.get(key));
        }
    }

    public static String javaObjectAsString(Object value) {

        try {
            ObjectMapper ow = new ObjectMapperProvider().get();
            return ow.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException("Exception while converting IPT Java Object to JsonString" + e);
        }
    }
}
