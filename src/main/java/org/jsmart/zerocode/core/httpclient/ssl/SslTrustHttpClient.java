package org.jsmart.zerocode.core.httpclient.ssl;

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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.utils.HelperJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import static org.jsmart.zerocode.core.utils.HelperJsonUtils.getContentAsItIsJson;

public class SslTrustHttpClient implements BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslTrustHttpClient.class);

    public static final String FILES_FIELD = "files";
    public static final String BOUNDARY_FIELD = "boundary";
    private boolean isFileUpload;
    private Object COOKIE_JSESSIONID_VALUE;

    private CloseableHttpClient httpclient;

    public SslTrustHttpClient() {
    }

    public SslTrustHttpClient(CloseableHttpClient httpclient) {
        this.httpclient = httpclient;
    }

    @Override
    public Response execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception {

        LOGGER.info("###Used SSL Enabled Http Client for both Http and Https connections");

        /** ---------------------------
         * Get the request body content
         * ----------------------------
         */
        String reqBodyAsString = getContentAsItIsJson(body);

        httpclient = createSslHttpClient();

        /** -----------------------
         * set the query parameters
         * ------------------------
         */
        if (queryParams != null) {
            httpUrl = setQueryParams(httpUrl, queryParams);
        }

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

        if (headers != null) {
            Map headersMap = headers;
            for (Object key : headersMap.keySet()) {
                removeDuplicateHeaders(requestBuilder, (String) key);
                requestBuilder.addHeader((String) key, (String) headersMap.get(key));
                LOGGER.info("Overrode header key:{}, with value:{}", key, headersMap.get(key));
            }

           isFileUpload = hasMultiPartHeader(headersMap);
        }

        // =-=-=-=-=-=-=-=-=-=-=-=-
        // Now execute the request
        // =-=-=-=-=-=-=-=-=-=-=-=-
        CloseableHttpResponse httpResponse;

        if (isFileUpload) {
            LOGGER.info("Zerocode - Preparing file upload...");

            RequestBuilder uploadRequestBuilder = createFileUploadRequestBuilder(httpUrl, methodName, reqBodyAsString);

            addCookieToHeader(uploadRequestBuilder);

            LOGGER.info("Zerocode - Executing file upload");

            httpResponse = httpclient.execute(uploadRequestBuilder.build());

        } else {

            addCookieToHeader(requestBuilder);

            httpResponse = httpclient.execute(requestBuilder.build());
        }

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

        serverResponse = responseBuilder.build();

        return serverResponse;
    }

    private void handleHttpSession(Response serverResponse, String headerKey) {
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

    private boolean hasMultiPartHeader(Map headersMap) {
        String contentType = (String) headersMap.get("content-type");
        return contentType != null ? contentType.contains("multipart/form-data") : false;
    }

    private RequestBuilder createFileUploadRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        Map<String, Object> fileFieldNameValueMap = getFileFieldNameValue(reqBodyAsString);

        List<String> fileFieldsList = (List<String>) fileFieldNameValueMap.get(FILES_FIELD);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        buildAllFilesToUpload(fileFieldsList, multipartEntityBuilder);

        buildOtherRequestParams(fileFieldNameValueMap, multipartEntityBuilder);

        buildMultiPartBoundary(fileFieldNameValueMap, multipartEntityBuilder);

        RequestBuilder uploadRequestBuilder = createUploadRequestBuilder(httpUrl, methodName, multipartEntityBuilder);

        return uploadRequestBuilder;
    }

    private RequestBuilder createUploadRequestBuilder(String httpUrl, String methodName, MultipartEntityBuilder multipartEntityBuilder) {

        RequestBuilder uploadRequestBuilder = RequestBuilder
                .create(methodName)
                .setUri(httpUrl);

        HttpEntity reqEntity = multipartEntityBuilder.build();

        uploadRequestBuilder.setEntity(reqEntity);

        return uploadRequestBuilder;
    }

    private void buildMultiPartBoundary(Map<String, Object> fileFieldNameValueMap, MultipartEntityBuilder multipartEntityBuilder) {
        String boundary = (String) fileFieldNameValueMap.get(BOUNDARY_FIELD);
        multipartEntityBuilder.setBoundary(boundary != null ? boundary : currentTimeMillis() + now().toString());
    }

    private void buildAllFilesToUpload(List<String> fileFiledsList, MultipartEntityBuilder multipartEntityBuilder) {
        fileFiledsList.forEach(fileField -> {
            String[] fieldNameValue = fileField.split(":");
            String fieldName = fieldNameValue[0];
            String fileNameWithPath = fieldNameValue[1].trim();

            FileBody fileBody = new FileBody(new File(getAbsPath(fileNameWithPath)));
            multipartEntityBuilder.addPart(fieldName, fileBody);
        });
    }

    private void buildOtherRequestParams(Map<String, Object> fileFieldNameValueMap, MultipartEntityBuilder multipartEntityBuilder) {
        for (Map.Entry<String, Object> entry : fileFieldNameValueMap.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getKey().equals(FILES_FIELD) || entry.getKey().equals(BOUNDARY_FIELD)) {
                continue;
            }
            multipartEntityBuilder.addPart(entry.getKey(), new StringBody((String) entry.getValue(), TEXT_PLAIN));
        }
    }

    private Map<String, Object> getFileFieldNameValue(String reqBodyAsString) throws IOException {
        return new ObjectMapperProvider().get().readValue(reqBodyAsString, HashMap.class);
    }

    private void removeDuplicateHeaders(RequestBuilder requestBuilder, String key) {
        if (requestBuilder.getFirstHeader(key) != null) {
            requestBuilder.removeHeaders(key);
        }
    }

    private CloseableHttpClient createSslHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    private String setQueryParams(String httpUrl, Map<String, Object> queryParams) {
        String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
        httpUrl = httpUrl + qualifiedQueryParams;
        return httpUrl;
    }

    private String createQualifiedQueryParams(Object queryParams) {
        String qualifiedQueryParam = "?";
        Map queryParamsMap = HelperJsonUtils.readHeadersAsMap(queryParams);
        for (Object key : queryParamsMap.keySet()) {
            if ("?".equals(qualifiedQueryParam)) {
                qualifiedQueryParam = qualifiedQueryParam + format("%s=%s", key, queryParamsMap.get(key));
            } else {
                qualifiedQueryParam = qualifiedQueryParam + format("&%s=%s", key, queryParamsMap.get(key));
            }
        }
        return qualifiedQueryParam;
    }

    private String getAbsPath(String filePath) {

        if (new File(filePath).exists()) {
            return filePath;
        }

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filePath);
        if (resource == null) {
            throw new RuntimeException("Could not get details of file or folder - `" + filePath + "`, does this exist?");
        }
        return resource.getPath();
    }
}

