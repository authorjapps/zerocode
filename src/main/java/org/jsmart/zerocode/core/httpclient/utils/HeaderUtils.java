package org.jsmart.zerocode.core.httpclient.utils;

import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.jsmart.zerocode.core.httpclient.BasicHttpClient.CONTENT_TYPE;
import static org.jsmart.zerocode.core.httpclient.BasicHttpClient.MULTIPART_FORM_DATA;

public class HeaderUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderUtils.class);

    public static void processFrameworkDefault(Map<String, Object> headers, RequestBuilder requestBuilder) {
        if (headers != null) {
            Map headersMap = headers;
            for (Object key : headersMap.keySet()) {
                if(CONTENT_TYPE.equalsIgnoreCase((String)key) && MULTIPART_FORM_DATA.equals(headersMap.get(key))){
                    continue;
                }
                removeDuplicateHeaders(requestBuilder, (String) key);
                requestBuilder.addHeader((String) key, (String) headersMap.get(key));
                LOGGER.info("Overridden the header key:{}, with value:{}", key, headersMap.get(key));
            }
        }
    }

    public static void removeDuplicateHeaders(RequestBuilder requestBuilder, String key) {
        if (requestBuilder.getFirstHeader(key) != null) {
            requestBuilder.removeHeaders(key);
        }
    }

    public static boolean hasMultiPartHeader(Map headersMap) {
        if(headersMap == null){
            return false;
        }
        String contentType = (String) headersMap.get(CONTENT_TYPE);
        return contentType != null ? contentType.contains(MULTIPART_FORM_DATA) : false;
    }
}
