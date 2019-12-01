package org.jsmart.zerocode.core.engine.executor.httpapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.jsmart.zerocode.core.domain.MockSteps;
import org.jsmart.zerocode.core.domain.Response;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithLocalMock;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithVirtuosoMock;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithWireMock;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class HttpApiExecutorImpl implements HttpApiExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiExecutorImpl.class);

    private final BasicHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Inject
    public HttpApiExecutorImpl(BasicHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Inject
    private SmartUtils smartUtils;

    @Inject(optional = true)
    @Named("mock.api.port")
    private int mockPort;

    @Override
    public String execute(String httpUrl, String methodName, String requestJson) throws Exception {

        HashMap queryParams = (HashMap) readJsonPathOrElseNull(requestJson, "$.queryParams");
        HashMap headers = (HashMap) readJsonPathOrElseNull(requestJson, "$.headers");
        Object bodyContent = readJsonPathOrElseNull(requestJson, "$.body");

        /*
         * $MOCK: Create mock endpoints supplied for this scenario
         */
        if (completedMockingEndPoints(httpUrl, requestJson, methodName, bodyContent)) {
            /*
             * All mocks done? Then return a success message
             */
            return "{\"status\": 200}";
        }

        final javax.ws.rs.core.Response serverResponse = httpClient.execute(httpUrl, methodName, headers, queryParams, bodyContent);

        /*
         * now read the response for :
         * - headers
         * - body
         * - status
         */
        final int responseStatus = serverResponse.getStatus();

        final MultivaluedMap responseHeaders = serverResponse.getMetadata();

        final String responseBodyAsString = (String) serverResponse.getEntity();

        Response zeroCodeResponse = deriveZeroCodeResponseFrom(responseStatus, responseHeaders, responseBodyAsString);

        final String zeroCodeResponseString = objectMapper.writeValueAsString(zeroCodeResponse);

        return prettyPrintJson(zeroCodeResponseString);
    }

    private Response deriveZeroCodeResponseFrom(int responseStatus,
                                                MultivaluedMap responseHeaders,
                                                String responseBodyAsString)
            throws IOException {

        final JsonNode jsonBody;
        final String rawBody;

        if (isEmpty(responseBodyAsString)) {
            jsonBody = null;
            rawBody = null;

        } else if (isParsableJson(responseBodyAsString)) {
            jsonBody = objectMapper.readValue(responseBodyAsString, JsonNode.class);
            rawBody = null;

        } else {
            jsonBody = null;
            rawBody = responseBodyAsString;

        }

        return new Response(responseStatus, responseHeaders, jsonBody, rawBody, null);
    }

    private boolean completedMockingEndPoints(String httpUrl, String requestJson, String methodName, Object bodyContent) throws java.io.IOException {
        if (httpUrl.contains("/$MOCK") && methodName.equals("$USE.WIREMOCK")) {

            MockSteps mockSteps = smartUtils.getMapper().readValue(requestJson, MockSteps.class);

            if (mockPort > 0) {
                createWithWireMock(mockSteps, mockPort);

                LOGGER.info("#SUCCESS: End points simulated via wiremock.");

                return true;
            }

            LOGGER.error("\n\n#DISABLED: Mocking was not activated as there was no port configured in the properties file. \n\n " +
                    "Usage: e.g. in your <env host config .properties> file provide- \n " +
                    "mock.api.port=8888\n\n");
            return false;
        } else if (httpUrl.contains("/$MOCK") && methodName.equals("$USE.VIRTUOSO")) {
            LOGGER.info("\n#body:\n" + bodyContent);

            //read the content of the "request". This contains the complete rest API.
            createWithVirtuosoMock(bodyContent != null ? bodyContent.toString() : null);

            LOGGER.info("#SUCCESS: End point simulated via virtuoso.");
            return true;
        } else if (httpUrl.contains("/$MOCK") && methodName.equals("$USE.SIMULATOR")) {
            LOGGER.info("\n#body:\n" + bodyContent);

            //read the content of the "request". This contains the complete rest API.
            createWithLocalMock(bodyContent != null ? bodyContent.toString() : null);

            LOGGER.info("#SUCCESS: End point simulated via local simulator.");

            return true;
        }
        return false;
    }

    private Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try {
            return JsonPath.read(requestJson, jsonPath);
        } catch (PathNotFoundException pEx) {
            LOGGER.debug("No " + jsonPath + " was present in the request. returned null.");
            return null;
        }
    }

    private boolean isParsableJson(String potentialJsonString) {
        try {
            objectMapper.readTree(potentialJsonString);
            return true;
        } catch (IOException e) {
            LOGGER.warn("\n---------------------------------------------\n\n"
                    + "\t\t\t\t\t\t * Warning *  \n\nOutput was not a valid JSON body. It was treated as a simple rawBody."
                    + " If it was intentional, you can ignore this warning. "
                    + "\n -OR- Update your assertions block with \"rawBody\" instead of \"body\" "
                    + "\n e.g. \"rawBody\" : \"an expected string \""
                    + "\n\n---------------------------------------------");
            return false;
        }
    }
}
