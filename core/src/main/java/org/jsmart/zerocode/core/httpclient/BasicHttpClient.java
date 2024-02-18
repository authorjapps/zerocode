package org.jsmart.zerocode.core.httpclient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.jsmart.zerocode.core.utils.HelperJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.buildAllFilesToUpload;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.buildMultiPartBoundary;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.buildOtherRequestParams;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.createUploadRequestBuilder;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.getFileFieldNameValue;
import static org.jsmart.zerocode.core.httpclient.utils.HeaderUtils.processFrameworkDefault;
import static org.jsmart.zerocode.core.httpclient.utils.UrlQueryParamsUtils.setQueryParams;
import static org.jsmart.zerocode.core.utils.HelperJsonUtils.getContentAsItIsJson;

public class BasicHttpClient {
    Logger LOGGER = LoggerFactory.getLogger(BasicHttpClient.class);

    public static final String FILES_FIELD = "files";
    public static final String BOUNDARY_FIELD = "boundary";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
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
     * return CloseableHttpClient
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

        LOGGER.debug("###Creating SSL Enabled Http Client for both http/https/TLS connections");

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
     * httpUrl     : path to end point
     * methodName  : e.g. GET, PUT etc
     * headers     : headers, cookies etc
     * queryParams : key-value query params after the ? in the url
     * body        : json body
     *
     * returns : Http response consists of status code, entity, headers, cookies etc
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
     * httpResponse  : Received Apache http response from the server
     *
     *   : Effective response with handled http session.
     * @throws IOException
     */
    public Response handleResponse(CloseableHttpResponse httpResponse) throws IOException {
        Response serverResponse = createCharsetResponse(httpResponse);

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
     * Override this method in case you want to make the Charset response differently for your project.
     * Otherwise the framework falls back to this implementation by default which means- If the Charset
     * is not set by the server framework will default to Charset.defaultCharset(), otherwise it will
     * use the Charset sent by the server e.g. UAT-8 or UTF-16 or UTF-32 etc.
     *
     * Note-
     * See the implementation of java.nio.charset.Charset#defaultCharset. Here the default is UTF-8 if the
     * defaultCharset is not set by the JVM, otherwise it picks the JVM provided defaultCharset
     *
     * httpResponse:
     * A http response compatible with Charset received from the http server e.g. UTF-8, UTF-16 etc
     *
     */
    public Response createCharsetResponse(CloseableHttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        Charset charset = ContentType.getOrDefault(httpResponse.getEntity()).getCharset();
        charset = (charset == null) ? Charset.defaultCharset() : charset;
        return Response
                .status(httpResponse.getStatusLine().getStatusCode())
                .entity(entity != null ? IOUtils.toString(entity.getContent(), charset) : null)
                .build();
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
     * httpUrl - Url of the target service
     * queryParams - Query parameters to pass
     * return : Effective url
     *
     */
    public String handleUrlAndQueryParams(String httpUrl, Map<String, Object> queryParams) throws URISyntaxException {
        if ((queryParams != null) && (!queryParams.isEmpty())) {
            httpUrl = setQueryParams(httpUrl, queryParams);
        }
        return httpUrl;
    }

    /**
     * The framework will fall back to this default implementation to handle the headers.
     * If you want to override any headers, you can do that by overriding the
     * amendRequestHeaders(headers) method.
     *
     * headers
     * requestBuilder
     * return : An effective Apache http request builder object with processed headers.
     */
    public RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder) {
        Map<String, Object> amendedHeaders = amendRequestHeaders(headers);
        processFrameworkDefault(amendedHeaders, requestBuilder);
        return requestBuilder;
    }

    /**
     * Override this method only in case you want to
     * - Add more headers to the http request or
     * - Amend or modify the headers which were supplied from the JSON test-case request step.
     *
     * headers : The headers passed from the JSON test step request
     * return : An effective headers map.
     */
    public Map<String, Object> amendRequestHeaders(Map<String, Object> headers) {
        return headers;
    }

    /**
     * Override this method when you want to manipulate the request body passed from your test cases.
     * Otherwise the framework falls back to this default implementation.
     * You can override this method via @UseHttpClient(YourCustomHttpClient.class)
     * body
     * return
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
     * You can override this method via @UseHttpClient(YourCustomHttpClient.class)
     *
     * httpUrl
     * methodName
     * reqBodyAsString
     * return
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
     * This is how framework makes the KeyValue pair when "application/x-www-form-urlencoded" headers
     * is passed in the request.  In case you want to build or prepare the requests differently,
     * you can override this method via @UseHttpClient(YourCustomHttpClient.class).
     *
     */
    public RequestBuilder createFormUrlEncodedRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);
        if (reqBodyAsString != null) {
            Map<String, Object> reqBodyMap = HelperJsonUtils.readObjectAsMap(reqBodyAsString);
            List<NameValuePair> reqBody = new ArrayList<>();
             for(String key : reqBodyMap.keySet()) {
                 reqBody.add(new BasicNameValuePair(key, reqBodyMap.get(key).toString()));
             }
             HttpEntity httpEntity = new UrlEncodedFormEntity(reqBody);
             requestBuilder.setEntity(httpEntity);
            requestBuilder.setHeader(CONTENT_TYPE, APPLICATION_FORM_URL_ENCODED);
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
     * You can override this method via @UseHttpClient(YourCustomHttpClient.class)
     *
     * httpUrl: The end pint
     * methodName: meaningful name of a method
     * reqBodyAsString:
     */
    public RequestBuilder createFileUploadRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        Map<String, Object> fileFieldNameValueMap = getFileFieldNameValue(reqBodyAsString);

        List<String> fileFieldsList = (List<String>) fileFieldNameValueMap.get(FILES_FIELD);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        /*
	 * Allow fileFieldsList to be null.
	 * fileFieldsList can be null if multipart/form-data is sent without any files
	 * Refer Issue #168 - Raised and fixed by santhoshTpixler
	 */
        if(fileFieldsList != null) {
        	buildAllFilesToUpload(fileFieldsList, multipartEntityBuilder);
	}

        buildOtherRequestParams(fileFieldNameValueMap, multipartEntityBuilder);

        buildMultiPartBoundary(fileFieldNameValueMap, multipartEntityBuilder);

        return createUploadRequestBuilder(httpUrl, methodName, multipartEntityBuilder);
    }

    /**
     * This method handles the http session to be maintained between the calls.
     * In case the session is not needed or to be handled differently, then this
     * method can be overridden to do nothing or to roll your own feature.
     *
     * serverResponse
     * headerKey
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
        // then it is taken care here.
        // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        if (COOKIE_JSESSIONID_VALUE != null) {
            uploadRequestBuilder.addHeader("Cookie", (String) COOKIE_JSESSIONID_VALUE);
        }
    }

    public RequestBuilder createRequestBuilder(String httpUrl, String methodName, Map<String, Object> headers, String reqBodyAsString) throws IOException {

        String contentType = headers != null? (String) headers.get(CONTENT_TYPE) :null;

        if(contentType!=null){

            if(contentType.equals(MULTIPART_FORM_DATA)){

                return createFileUploadRequestBuilder(httpUrl, methodName, reqBodyAsString);

            } else if(contentType.equals(APPLICATION_FORM_URL_ENCODED)) {

                return createFormUrlEncodedRequestBuilder(httpUrl, methodName, reqBodyAsString);
            }
            // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            // Extension - Any other header types to be specially handled here
            // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            // else if(contentType.equals("OTHER-TYPES")){
            //    Handling logic
            // }
        }
        return createDefaultRequestBuilder(httpUrl, methodName, reqBodyAsString);
    }
}
