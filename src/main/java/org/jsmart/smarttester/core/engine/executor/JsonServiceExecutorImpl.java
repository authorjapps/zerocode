package org.jsmart.smarttester.core.engine.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jsmart.smarttester.core.engine.mocker.RestEndPointMocker;
import org.jsmart.smarttester.core.logbuilder.LogCorelationshipPrinter;
import org.jsmart.smarttester.core.utils.HomeOfficeJsonUtils;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;

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
    LogCorelationshipPrinter logCorelationshipPrinter = LogCorelationshipPrinter.newInstance(logger);

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

            return resultJson;

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    public String executeRESTService(String urlName, String methodName, String requestJson) {

        try {
            String responseJson =  executeRESTInternal(urlName, methodName, requestJson);

            return responseJson;

        } catch (Exception exc) {

            throw new RuntimeException(exc);

        } finally {
            //logCorelationshipPrinter.print();
        }
    }


    private String executeRESTInternal(String httpUrl, String methodName, String requestJson) throws Exception {
        String locationHref = null;
        String resultBodyContent = null;
        int httpResponseCode = 0;

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
        //


        // TODO: String restMethodParam = smartUtils.getContentAsItIsJson(bodyContent);
        String restMethodParam = HomeOfficeJsonUtils.getContentAsItIsJson(bodyContent);

        //set the query parameters
        if(queryParams != null){
            String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
            httpUrl = httpUrl + qualifiedQueryParams;
        }

        ClientRequest clientExecutor = httpClientExecutor.createRequest(httpUrl);

        //set headers
        if(headers != null){
            setRequestHeaders(headers, clientExecutor);
        }
        if(COOKIE_JSESSIONID_VALUE != null) {
            clientExecutor.header("Cookie", COOKIE_JSESSIONID_VALUE);
        }

        // set the body
        if(restMethodParam != null){
            clientExecutor.body("application/json", restMethodParam);
        }

        // set GET POST PUT DELETE
        // TODO: if none of these then throw exception
        clientExecutor.setHttpMethod(methodName);

        // now execute
        ClientResponse serverResponse = clientExecutor.execute();

        httpResponseCode = serverResponse.getResponseStatus().getStatusCode();
        resultBodyContent = (String) serverResponse.getEntity(String.class);
        locationHref = serverResponse.getLocation() != null? serverResponse.getLocation().getHref(): null;

        Set headerKeySet = serverResponse.getHeaders().keySet();
        for(Object key: headerKeySet){
            if("Set-Cookie".equals(key) ) {
                COOKIE_JSESSIONID_VALUE = serverResponse.getHeaders().get(key);
            }
        }

        String resultForAssertion = HomeOfficeJsonUtils.createAndReturnAssertionResultJson(httpResponseCode, resultBodyContent, locationHref);
        //String resultForAssertion = HomeOfficeJsonUtils.javaObjectAsString(serverResponse);

        return resultForAssertion;
    }

    private Object readJsonPathOrElseNull(String requestJson, String jsonPath) {
        try{
            return JsonPath.read(requestJson, jsonPath);
        } catch(PathNotFoundException pEx){
            logger.warn("No " + jsonPath + " was present in the request.");
            return  null;
        }
    }

    private String createQualifiedQueryParams(Object queryParams) {
        String qualifiedQueryParam = "?";
        Map queryParamsMap = HomeOfficeJsonUtils.readHeadersAsMap(queryParams);
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
        //Map headersMap = HomeOfficeJsonUtils.readHeadersAsMap(headers);
        Map headersMap = (HashMap)headers;
        for(Object key: headersMap.keySet()){
            clientExecutor.header((String)key, headersMap.get(key));
        }

        return clientExecutor;
    }

    public void setHttpClientExecutor(ApacheHttpClientExecutor httpClientExecutor) {
        this.httpClientExecutor = httpClientExecutor;
    }

}
