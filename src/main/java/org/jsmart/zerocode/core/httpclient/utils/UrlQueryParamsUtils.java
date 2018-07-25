package org.jsmart.zerocode.core.httpclient.utils;

import org.jsmart.zerocode.core.utils.HelperJsonUtils;

import java.util.Map;

import static java.lang.String.format;

public class UrlQueryParamsUtils {

    public static String setQueryParams(String httpUrl, Map<String, Object> queryParams) {
        String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
        httpUrl = httpUrl + qualifiedQueryParams;
        return httpUrl;
    }

    private static String createQualifiedQueryParams(Object queryParams) {
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

}
