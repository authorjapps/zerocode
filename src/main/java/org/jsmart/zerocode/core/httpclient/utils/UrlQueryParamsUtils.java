package org.jsmart.zerocode.core.httpclient.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.lang.String.format;

public class UrlQueryParamsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlQueryParamsUtils.class);

    public static String setQueryParams(String httpUrl, Map<String, Object> queryParams) {
        String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
        httpUrl = httpUrl + qualifiedQueryParams;

        LOGGER.info("### Effective url is : " + httpUrl);
        return httpUrl;
    }

    protected static String createQualifiedQueryParams(Map<String, Object> queryParamsMap) {
        String qualifiedQueryParam = "";
        for (Object key : queryParamsMap.keySet()) {
            if ("".equals(qualifiedQueryParam)) {
                qualifiedQueryParam = "?" + format("%s=%s", key, queryParamsMap.get(key));
            } else {
                qualifiedQueryParam = qualifiedQueryParam + format("&%s=%s", key, queryParamsMap.get(key));
            }
        }
        LOGGER.info("### qualifiedQueryParams : " + qualifiedQueryParam);
        return qualifiedQueryParam;
    }

}
