package org.jsmart.smarttester.core.engine.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jsmart.smarttester.core.domain.Response;
import org.jsmart.smarttester.core.engine.mocker.RestEndPointMocker;
import org.jsmart.smarttester.core.utils.HelperJsonUtils;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.jsmart.smarttester.core.utils.HelperJsonUtils.getContentAsItIsJson;
import static org.jsmart.smarttester.core.utils.SmartUtils.prettyPrintJson;

public class JsonServiceExecutorImpl implements JsonServiceExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JsonServiceExecutorImpl.class);

    //guice
    @Inject
    private JavaExecutor javaExecutor;
    @Inject
    private ApacheHttpClientExecutor httpClientExecutor;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    SmartUtils smartUtils;
    //guice

    private Object COOKIE_JSESSIONID_VALUE;

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

        Object queryParams = readJsonPathOrElseNull(requestJson, "$.queryParams");
        Object headers = readJsonPathOrElseNull(requestJson, "$.headers");
        Object bodyContent = readJsonPathOrElseNull(requestJson, "$.body");

        //
        if(httpUrl.contains("/$VIRTUOSO")){
            logger.info("\n#body:\n" + bodyContent);

            //read the content of the "request". This contains the complete rest API.
            RestEndPointMocker.createVirtuosoMock(bodyContent != null ? bodyContent.toString() : null);

            logger.info("#SUCCESS: End point simulated.");
            return "{\"status\": 200}";
        }

        if(httpUrl.contains("/$MOCK")){
            logger.info("\n#body:\n" + bodyContent);

            //read the content of the "request". This contains the complete rest API.
            RestEndPointMocker.createLocalMock(bodyContent != null ? bodyContent.toString() : null);

            logger.info("#SUCCESS: End point simulated.");
            return "{\"status\": 200}";
        }

        /*
         * Get the request body content
         */
        String reqBodyAsString = getContentAsItIsJson(bodyContent);

        /*
         * set the query parameters
         */
        if(queryParams != null){
            String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
            httpUrl = httpUrl + qualifiedQueryParams;
        }
        ClientRequest clientExecutor = httpClientExecutor.createRequest(httpUrl);

        /*
         * set the headers
         */
        if(headers != null){
            setRequestHeaders(headers, clientExecutor);
        }

        /*
         * Highly discouraged to use sessions, but in case any server uses session,
         * then it's taken care here.
         */
        if(COOKIE_JSESSIONID_VALUE != null) {
            clientExecutor.header("Cookie", COOKIE_JSESSIONID_VALUE);
        }

        /*
         * set the request body
         */
        if(reqBodyAsString != null){
            clientExecutor.body("application/json", reqBodyAsString);
        }

        // TODO: if none of the [GET POST PUT DELETE] then throw exception
        clientExecutor.setHttpMethod(methodName);

        /*
         * now execute the request
         */
        ClientResponse serverResponse = clientExecutor.execute();

        /*
         * now read the response for :
         * - headers
         * - body
         * - status
         */
        final int responseStatus = serverResponse.getResponseStatus().getStatusCode();

        final MultivaluedMap responseHeaders = serverResponse.getHeaders();

        final String respBodyAsString = (String)serverResponse.getEntity(String.class);
        final JsonNode bodyAsNode;
        if(StringUtils.isEmpty(respBodyAsString)){
            bodyAsNode = null;
        } else {
            bodyAsNode = objectMapper.readValue(respBodyAsString, JsonNode.class);
        }

        // location is already part of header, but in an array
        // final String locationHref = serverResponse.getLocation() != null? serverResponse.getLocation().getHref(): null;

        Response response = new Response(responseStatus, responseHeaders, bodyAsNode, null);
        final String relevantResponse = objectMapper.writeValueAsString(response);

        Set headerKeySet = serverResponse.getHeaders().keySet();
        for(Object key: headerKeySet){
            if("Set-Cookie".equals(key) ) {
                COOKIE_JSESSIONID_VALUE = serverResponse.getHeaders().get(key);
            }
        }

        return prettyPrintJson(relevantResponse);
    }

    private Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try{
            return JsonPath.read(requestJson, jsonPath);
        } catch(PathNotFoundException pEx){
            logger.debug("No " + jsonPath + " was present in the request. returned null.");
            return  null;
        }
    }

    private String createQualifiedQueryParams(Object queryParams) {
        String qualifiedQueryParam = "?";
        Map queryParamsMap = HelperJsonUtils.readHeadersAsMap(queryParams);
        for(Object key: queryParamsMap.keySet()){
            if("?".equals(qualifiedQueryParam)){
                qualifiedQueryParam = qualifiedQueryParam + format("%s=%s", (String)key, (String)queryParamsMap.get(key));
            }
            else{
                qualifiedQueryParam = qualifiedQueryParam + format("&%s=%s", (String)key, (String)queryParamsMap.get(key));
            }
        }
        return qualifiedQueryParam;
    }

    private ClientRequest setRequestHeaders(Object headers, ClientRequest clientExecutor) {
        Map headersMap = (HashMap)headers;
        for(Object key: headersMap.keySet()){
            clientExecutor.header((String)key, headersMap.get(key));
        }

        return clientExecutor;
    }

    public void setHttpClientExecutor(ApacheHttpClientExecutor httpClientExecutor) {
        this.httpClientExecutor = httpClientExecutor;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
