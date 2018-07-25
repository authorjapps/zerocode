package org.jsmart.zerocode.core.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.jsmart.zerocode.core.httpclient.utils.FileUploadUtils.*;
import static org.jsmart.zerocode.core.utils.HelperJsonUtils.getContentAsItIsJson;

public interface BasicHttpClient {
    public static final String FILES_FIELD = "files";
    public static final String BOUNDARY_FIELD = "boundary";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String CONTENT_TYPE = "content-type";

    /**
     * @param httpUrl     : path to end point
     * @param methodName  : e.g. GET, PUT etc
     * @param headers     : headers, cookies etc
     * @param queryParams : key-value query params after the '?' in the url
     * @param body        : json body
     * @return : RestEasy http response consists of status code, entity, headers etc
     * @throws Exception
     */
    Response execute(String httpUrl,
                     String methodName,
                     Map<String, Object> headers,
                     Map<String, Object> queryParams,
                     Object body) throws Exception;

    /**
     *
     * @param httpUrl
     * @param queryParams
     * @return
     */
    String handleUrlAndQueryParams(String httpUrl, Map<String, Object> queryParams);

    /**
     * Override this method in case you want to handle the headers passed from the testcase request differently.
     * If there are keys with same name in case of some headers were populated from properties file,
     * then how these should be handled. The framework will fall back to this implementation to handle
     * this.
     * @param headers
     * @param requestBuilder
     * @return
     */
    RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder);


    default String handleRequestBody(Object body) {
        return getContentAsItIsJson(body);
    }

    Response handleResponse(CloseableHttpResponse httpResponse) throws IOException;

    default RequestBuilder createDefaultRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) {
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

    default RequestBuilder createFileUploadRequestBuilder(String httpUrl, String methodName, String reqBodyAsString) throws IOException {
        Map<String, Object> fileFieldNameValueMap = getFileFieldNameValue(reqBodyAsString);

        List<String> fileFieldsList = (List<String>) fileFieldNameValueMap.get(FILES_FIELD);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        buildAllFilesToUpload(fileFieldsList, multipartEntityBuilder);

        buildOtherRequestParams(fileFieldNameValueMap, multipartEntityBuilder);

        buildMultiPartBoundary(fileFieldNameValueMap, multipartEntityBuilder);

        return createUploadRequestBuilder(httpUrl, methodName, multipartEntityBuilder);
    }


}
