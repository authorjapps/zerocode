package org.jsmart.zerocode.core.httpclient;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jsmart.zerocode.core.utils.HelperJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.jsmart.zerocode.core.utils.HelperJsonUtils.getContentAsItIsJson;

public class RestEasyDefaultHttpClient implements BasicHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(RestEasyDefaultHttpClient.class);

    private static ApacheHttpClientExecutor httpClientExecutor = new ApacheHttpClientExecutor();

    private ClientRequest clientExecutor;

    private Object COOKIE_JSESSIONID_VALUE;

    @Override
    public Response execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception {
        logger.info("###Used RestEasyDefaultHttpClient");
        /*
         * Get the request body content
         */
        String reqBodyAsString = getContentAsItIsJson(body);

        /*
         * set the query parameters
         */
        if(queryParams != null){
            String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
            httpUrl = httpUrl + qualifiedQueryParams;
        }

        /*
         * set the end point with query params
         */
        clientExecutor = httpClientExecutor.createRequest(httpUrl);

        /*
         * set the headers
         */
        if(headers != null){
            setRequestHeaders(headers, clientExecutor);
        }

        /*
         * Setting cookies:
         *
         * Highly discouraged to use sessions, but in case of any server dependent upon session,
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

        // TODO: if none of the [GET POST PUT DELETE] then throw exception. Raise an issue.
        clientExecutor.setHttpMethod(methodName);

        /*
         * now execute the request
         */
        ClientResponse serverResponsePre = clientExecutor.execute();

        Response serverResponse = Response
                .status(serverResponsePre.getStatus())
                .entity(serverResponsePre.getEntity(String.class))
                .build();


        final MultivaluedMap headersMap = serverResponsePre.getHeaders();
        final Iterator iterator = headersMap.keySet().iterator();
        while(iterator.hasNext()){

            Object key = iterator.next();
            serverResponse = Response.fromResponse(serverResponse).header((String)key, headersMap.get(key)).build();

        }

        Set headerKeySet = serverResponse.getMetadata().keySet();

        for(Object key: headerKeySet){
            if("Set-Cookie".equals(key) ) {
                COOKIE_JSESSIONID_VALUE = serverResponse.getMetadata().get(key);
            }
        }

        return serverResponse;
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

}
