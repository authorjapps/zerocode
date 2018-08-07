package org.jsmart.zerocode.core.httpclient;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.*;
import static org.jsmart.zerocode.core.httpclient.utils.HeaderUtils.hasMultiPartHeader;
import static org.jsmart.zerocode.core.httpclient.utils.HeaderUtils.processFrameworkDefault;
import static org.jsmart.zerocode.core.httpclient.utils.UrlQueryParamsUtils.setQueryParams;
import static org.jsmart.zerocode.core.utils.HelperJsonUtils.getContentAsItIsJson;

public class BasicHttpClient {
    Logger LOGGER = LoggerFactory.getLogger(BasicHttpClient.class);

    public static final String FILES_FIELD = "files";
    public static final String BOUNDARY_FIELD = "boundary";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String CONTENT_TYPE = "Content-Type";

    private Object COOKIE_JSESSIONID_VALUE;
    private CloseableHttpClient httpclient;

    public BasicHttpClient() {
    }

    public BasicHttpClient(CloseableHttpClient httpclient) {
        this.httpclient = httpclient;
    }

    /**
     * Override this method to create your own http or https client or a customized client if needed
     * for your project. Framework uses the below client which is the default implementation.
     * - org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient#createHttpClient()
     * {@code
     * See examples:
     * - org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient#createHttpClient()
     * - org.jsmart.zerocode.core.httpclient.ssl.SslTrustCorporateProxyHttpClient#createHttpClient()
     * - org.jsmart.zerocode.core.httpclient.ssl.CorporateProxyNoSslContextHttpClient#createHttpClient()
     * }
     *
     * @return CloseableHttpClient
     * @throws Exception
     */
    public CloseableHttpClient createHttpClient() throws Exception {
        /*
         * If your connections are not via SSL or corporate Proxy etc,
         * You can simply override this method and return the below default
         * client provided by "org.apache.httpcomponents.HttpClients".
         *
         *   - return HttpClients.createDefault();
         */

        LOGGER.info("###Creating SSL Enabled Http Client for both http/https/TLS connections");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .build();

    }

    /**
     * Override this method in case you want to execute the http call differently via your http client.
     * Otherwise the framework falls back to this implementation by default.
     *
     * @param httpUrl     : path to end point
     * @param methodName  : e.g. GET, PUT etc
     * @param headers     : headers, cookies etc
     * @param queryParams : key-value query params after the '?' in the url
     * @param body        : json body
     *
     * @return : Http response consists of status code, entity, headers, cookies etc
     * @throws Exception
     */
    public Response execute(String httpUrl,
                            String methodName,
                            Map<String, Object> headers,
                            Map<String, Object> queryParams,
                            Object body) throws Exception {

        httpclient = createHttpClient();

        // ---------------------------
        // Handle request body content
        // ---------------------------
        String reqBodyAsString = handleRequestBody(body);

        // -----------------------------------
        // Handle the url and query parameters
        // -----------------------------------
        httpUrl = handleUrlAndQueryParams(httpUrl, queryParams);

        RequestBuilder requestBuilder = createRequestBuilder(httpUrl, methodName, headers, reqBodyAsString);

        // ------------------
        // Handle the headers
        // ------------------
        handleHeaders(headers, requestBuilder);

        // ------------------
        // Handle cookies
        // ------------------
        addCookieToHeader(requestBuilder);

        CloseableHttpResponse httpResponse = httpclient.execute(requestBuilder.build());

        // --------------------
        // Handle the response
        // --------------------
        return handleResponse(httpResponse);
    }

    /**
     * Once the client executes the http call, then it receives the http response. This method takes care of handling
     * that. In case you need to handle it differently you can override this method.
     *
     * @param httpResponse  : Received Apache http response from the server
     *
     * @return  : Effective response with handled http session.
     * @throws IOException
     */
    public Response handleResponse(CloseableHttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        Response serverResponse = Response
                .status(httpResponse.getStatusLine().getStatusCode())
                .entity(entity != null ? IOUtils.toString(entity.getContent()) : null)
                .build();

        Header[] allHeaders = httpResponse.getAllHeaders();
        Response.ResponseBuilder responseBuilder = Response.fromResponse(serverResponse);
        for (Header thisHeader : allHeaders) {
            String headerKey = thisHeader.getName();
            responseBuilder = responseBuilder.header(headerKey, thisHeader.getValue());

            handleHttpSession(serverResponse, headerKey);
        }

        return responseBuilder.build();
    }

    /**
     * If(optionally) query parameters was sent as a JSON in the request below, this gets available to this method
     * for processing them with the url.
     *<pre>{@code
     * e.g.
     * "url": "/api/v1/search/people"
     * "request": {
     *     "queryParams": {
     *         "city":"Lon",
     *         "lang":"Awesome"
     *     }
     * }
     * }</pre>
     * will resolve to effective url "/api/v1/search/people?city=Lon{@literal &}lang=Awesome".
     *
     * In case you need to handle it differently you can override this method to change this behaviour to roll your own
     * feature.
     *
     * @param httpUrl - Url of the target service
     * @param queryParams - Query parameters to pass
     * @return : Effective url
     */
    public String handleUrlAndQueryParams(String httpUrl, Map<String, Object> queryParams) {
        if (queryParams != null) {
            httpUrl = setQueryParams(httpUrl, queryParams);
        }
        return httpUrl;
    }

    /**
     * Override this method in case you want to handle the headers differently which were passed from the
     * test-case requests. If there are keys with same name e.g. some headers were populated from
     * properties file(or via any other way from your java application), then how these should be handled.
     * The framework will fall back to this default implementation to handle this.
     *
     * @param headers
     * @param requestBuilder
     * @return : An effective Apache http request builder object with processed headers.
     */
    public RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder) {
        processFrameworkDefault(headers, requestBuilder);
        return requestBuilder;
    }

    /**
     * Override this method when you want to manipulate the request body passed from your test cases.
     * Otherwise the framework falls back to this default implementation.
     *
     * @param body
     * @return
     */
    public String handleRequestBody(Object body) {
        return getContentAsItIsJson(body);
    }

    /**
     * This is the usual http request builder most widely used using Apache Http Client. In case you want to build
     * or prepare the requests differently, you can override this method.
     *
     * Please see the following request builder to handle file uploads.
     *     - BasicHttpClient#createFileUploadRequestBuilder(java.lang.String, java.lang.String, java.lang.String)
     *
     * @param httpUrl
     * @param methodName
     * @param reqBodyAsString
     * @return
     */
    public RequestBuilder createDefaultRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) {
        RequestBuilder requestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);

        if (reqBodyAsString != null) {
            HttpEntity httpEntity = EntityBuilder.create()
                    .setContentType(APPLICATION_JSON)
                    .setText(reqBodyAsString)
                    .build();
            requestBuilder.setEntity(httpEntity);
        }
        return requestBuilder;
    }

    /**
     * This is the http request builder for file uploads, using Apache Http Client. In case you want to build
     * or prepare the requests differently, you can override this method.
     *
     * Note-
     * With file uploads you can send more headers too from the testcase to the server, except "Content-Type" because
     * this is reserved for "multipart/form-data" which the client sends to server during the file uploads. You can
     * also send more request-params and "boundary" from the test cases if needed. The boundary defaults to an unique
     * string of local-date-time-stamp if not provided in the request.
     *
     * @param httpUrl
     * @param methodName
     * @param reqBodyAsString
     * @return
     * @throws IOException
     */
    public RequestBuilder createFileUploadRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        Map<String, Object> fileFieldNameValueMap = getFileFieldNameValue(reqBodyAsString);

        List<String> fileFieldsList = (List<String>) fileFieldNameValueMap.get(FILES_FIELD);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        buildAllFilesToUpload(fileFieldsList, multipartEntityBuilder);

        buildOtherRequestParams(fileFieldNameValueMap, multipartEntityBuilder);

        buildMultiPartBoundary(fileFieldNameValueMap, multipartEntityBuilder);

        return createUploadRequestBuilder(httpUrl, methodName, multipartEntityBuilder);
    }

    /**
     * This method handles the http session to be maintained between the calls.
     * In case the session is not needed or to be handled differently, then this
     * method can be overridden to do nothing or to roll your own feature.
     *
     * @param serverResponse
     * @param headerKey
     */
    public void handleHttpSession(Response serverResponse, String headerKey) {
        /** ---------------
         * Session handled
         * ----------------
         */
        if ("Set-Cookie".equals(headerKey)) {
            COOKIE_JSESSIONID_VALUE = serverResponse.getMetadata().get(headerKey);
        }
    }

    private void addCookieToHeader(RequestBuilder uploadRequestBuilder) {
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        // Setting cookies:
        // Highly discouraged to use sessions, but in case of any server dependent upon session,
        // then it's taken care here.
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        if (COOKIE_JSESSIONID_VALUE != null) {
            uploadRequestBuilder.addHeader("Cookie", (String) COOKIE_JSESSIONID_VALUE);
        }
    }

    private RequestBuilder createRequestBuilder(String httpUrl, String methodName, Map<String, Object> headers, String reqBodyAsString) throws IOException {
        RequestBuilder requestBuilder;
        if (hasMultiPartHeader(headers)) {
            requestBuilder = createFileUploadRequestBuilder(httpUrl, methodName, reqBodyAsString);
        } else {
            requestBuilder = createDefaultRequestBuilder(httpUrl, methodName, reqBodyAsString);
        }
        return requestBuilder;
    }
}
