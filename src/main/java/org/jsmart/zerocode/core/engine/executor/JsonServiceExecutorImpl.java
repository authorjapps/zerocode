package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.domain.MockSteps;
import org.jsmart.zerocode.core.domain.Response;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;

import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithLocalMock;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithVirtuosoMock;
import static org.jsmart.zerocode.core.engine.mocker.RestEndPointMocker.createWithWireMock;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class JsonServiceExecutorImpl implements JsonServiceExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JsonServiceExecutorImpl.class);

    //guice
    @Inject
    private JavaExecutor javaExecutor;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    SmartUtils smartUtils;
    @Inject
    BasicHttpClient httpClient;
    //guice

    public JsonServiceExecutorImpl() {
    }

    public String executeJavaService(String serviceName, String methodName, String requestJson) throws JsonProcessingException {

        if( javaExecutor == null) {
            throw new RuntimeException("Can not proceed as the framework could not load the executors. ");
        }

        List<Class<?>> argumentTypes = javaExecutor.argumentTypes(serviceName, methodName);

        try {
            Object request = objectMapper.readValue(requestJson, argumentTypes.get(0));
            Object result = javaExecutor.execute(serviceName, methodName, request);

            final String resultJson = objectMapper.writeValueAsString(result);

            return prettyPrintJson(resultJson);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    public String executeRESTService(String urlName, String methodName, String requestJson) {

        try {
            String responseJson =  executeRESTInternal(urlName, methodName, requestJson);

            return responseJson;

        } catch (Exception exc) {

            exc.printStackTrace();

            throw new RuntimeException(exc);

        }
    }

    private String executeRESTInternal(String httpUrl, String methodName, String requestJson) throws Exception {

        HashMap queryParams = (HashMap) readJsonPathOrElseNull(requestJson, "$.queryParams");
        HashMap headers = (HashMap) readJsonPathOrElseNull(requestJson, "$.headers");
        Object bodyContent = readJsonPathOrElseNull(requestJson, "$.body");

        final javax.ws.rs.core.Response serverResponse = httpClient.execute(httpUrl, methodName, headers, queryParams, bodyContent);

        /*
         * $MOCK: Create mock endpoints supplied for this scenario
         */
        if (completedMockingEndPoints(httpUrl, requestJson, methodName, bodyContent)) {
            /*
             * All mocks done? Then return a success message
             */
            return "{\"status\": 200}";
        }

        /*
         * now read the response for :
         * - headers
         * - body
         * - status
         */
        final int responseStatus = serverResponse.getStatus();

        final MultivaluedMap responseHeaders = serverResponse.getMetadata();

        final String respBodyAsString = (String)serverResponse.getEntity();
        final JsonNode bodyAsNode;
        if(StringUtils.isEmpty(respBodyAsString)){
            bodyAsNode = null;
        } else {
            bodyAsNode = objectMapper.readValue(respBodyAsString, JsonNode.class);
        }

        Response response = new Response(responseStatus, responseHeaders, bodyAsNode, null);
        final String relevantResponse = objectMapper.writeValueAsString(response);

        return prettyPrintJson(relevantResponse);
    }

    private boolean completedMockingEndPoints(String httpUrl, String requestJson, String methodName, Object bodyContent) throws java.io.IOException {
        if(httpUrl.contains("/$MOCK") && methodName.equals("$USE.WIREMOCK")){

            MockSteps mockSteps = smartUtils.getMapper().readValue(requestJson, MockSteps.class);

            createWithWireMock(mockSteps);

            logger.info("#SUCCESS: End points simulated via wiremock.");

            return true;
        }

        else if(httpUrl.contains("/$MOCK") && methodName.equals("$USE.VIRTUOSO")){
            logger.info("\n#body:\n" + bodyContent);

            //read the content of the "request". This contains the complete rest API.
            createWithVirtuosoMock(bodyContent != null ? bodyContent.toString() : null);

            logger.info("#SUCCESS: End point simulated via virtuoso.");
            return true;
        }

        else if(httpUrl.contains("/$MOCK") && methodName.equals("$USE.SIMULATOR")){
            logger.info("\n#body:\n" + bodyContent);

            //read the content of the "request". This contains the complete rest API.
            createWithLocalMock(bodyContent != null ? bodyContent.toString() : null);

            logger.info("#SUCCESS: End point simulated via local simulator.");

            return true;
        }
        return false;
    }

    private Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try{
            return JsonPath.read(requestJson, jsonPath);
        } catch(PathNotFoundException pEx){
            logger.debug("No " + jsonPath + " was present in the request. returned null.");
            return  null;
        }
    }

}
