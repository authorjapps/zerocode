package org.jsmart.zerocode.core.httpclient.ssl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.utils.HelperJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static java.lang.String.format;
import static org.jsmart.zerocode.core.utils.HelperJsonUtils.getContentAsItIsJson;

public class CorporateProxyNoSslContextHttpClient implements BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CorporateProxyNoSslContextHttpClient.class);

    @Inject
    @Named("corporate.proxy.host")
    private String proxyHost;

    @Inject
    @Named("corporate.proxy.port")
    private int proxyPort;

    @Inject
    @Named("corporate.proxy.username")
    private String proxyUserName;

    @Inject
    @Named("corporate.proxy.password")
    private String proxyPassword;

    private boolean hasFilesToUpload;
    private Object COOKIE_JSESSIONID_VALUE;

    @Override
    public String handleUrlAndQueryParams(String httpUrl, Map<String, Object> queryParams) {
        if (queryParams != null) {
            httpUrl = setQueryParams(httpUrl, queryParams);
        }
        return httpUrl;
    }

    @Override
    public RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder) {
        if (headers != null) {
            Map headersMap = headers;
            for (Object key : headersMap.keySet()) {
                removeDuplicateHeaders(requestBuilder, (String) key);
                requestBuilder.addHeader((String) key, (String) headersMap.get(key));
                LOGGER.info("Overridden the header key:{}, with value:{}", key, headersMap.get(key));
            }

            hasFilesToUpload = hasMultiPartHeader(headersMap);
        }

        return requestBuilder;
    }

    @Override
    public String handleRequestBody(Object body) {
        return getContentAsItIsJson(body);
    }

    @Override
    public Response execute(String httpUrl, String methodName, Map<String, Object> headers, Map<String, Object> queryParams, Object body) throws Exception {

        LOGGER.info("###SOAP Http Client with Corporate Proxy enabled via SSL");

        CloseableHttpClient httpclient = createSslHttpClient();

        // ---------------------------
        // Handle request body content
        // ---------------------------
        String reqBodyAsString = handleRequestBody(body);

        // -----------------------------------
        // Handle the url and query parameters
        // -----------------------------------
        httpUrl = handleUrlAndQueryParams(httpUrl, queryParams);

        // -------------------------------------
        // Create default apache request builder
        // -------------------------------------
        RequestBuilder requestBuilder = createDefaultRequestBuilder(httpUrl, methodName, reqBodyAsString);

        // ------------------
        // Handle the headers
        // ------------------
        handleHeaders(headers, requestBuilder);

        CloseableHttpResponse httpResponse;
        // =-=-=-=-=-=-=-=-=-=
        // Execute the request
        // =-=-=-=-=-=-=-=-=-=
        if (hasFilesToUpload) {
            LOGGER.info("Zerocode - Preparing file upload...");

            RequestBuilder uploadRequestBuilder = createFileUploadRequestBuilder(httpUrl, methodName, reqBodyAsString);

            addCookieToHeader(uploadRequestBuilder);

            LOGGER.info("Zerocode - Executing file upload");

            httpResponse = httpclient.execute(uploadRequestBuilder.build());

        } else {

            addCookieToHeader(requestBuilder);

            httpResponse = httpclient.execute(requestBuilder.build());
        }

        // --------------------
        // Handle the response
        // --------------------
        return handleResponse(httpResponse);
    }

    @Override
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

    // -=-=-=-=-=-=-=-=-=-=-
    //
    // Private methods area
    //
    // -=-=-=-=-=-=-=-=-=-=-
    private void handleHttpSession(Response serverResponse, String headerKey) {
        /** ---------------
         * Session handled
         * ----------------
         */
        if ("Set-Cookie".equals(headerKey)) {
            COOKIE_JSESSIONID_VALUE = serverResponse.getMetadata().get(headerKey);
        }
    }

    private CloseableHttpClient createSslHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        //SSLContext sslContext = new SSLContextBuilder()
        //        .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        CredentialsProvider credsProvider = createProxyCredentialsProvider(proxyHost, proxyPort, proxyUserName, proxyPassword);

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);

        return HttpClients.custom()
                //.setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(proxy)
                .build();
    }

    private CredentialsProvider createProxyCredentialsProvider(String proxyHost, int proxyPort, String proxyUserName, String proxyPassword) {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(
                new AuthScope(proxyHost, proxyPort),
                new UsernamePasswordCredentials(proxyUserName, proxyPassword));

        return credsProvider;

    }

    private boolean hasMultiPartHeader(Map headersMap) {
        String contentType = (String) headersMap.get("content-type");
        return contentType != null ? contentType.contains("multipart/form-data") : false;
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

    private void removeDuplicateHeaders(RequestBuilder requestBuilder, String key) {
        if(requestBuilder.getFirstHeader(key) != null) {
            requestBuilder.removeHeaders(key);
        }
    }
}

