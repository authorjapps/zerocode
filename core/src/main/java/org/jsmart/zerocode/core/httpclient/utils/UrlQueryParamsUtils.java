package org.jsmart.zerocode.core.httpclient.utils;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class UrlQueryParamsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlQueryParamsUtils.class);

    public static String setQueryParams(final String httpUrl, final Map<String, Object> queryParams) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(httpUrl);
        Map<String, Object> nullSafeQueryParams = ofNullable(queryParams).orElseGet(HashMap::new);
        nullSafeQueryParams.keySet().forEach(key ->
                uriBuilder.addParameter(key, nullSafeQueryParams.get(key).toString())
        );
        String composedURL = uriBuilder.build().toString();
        LOGGER.debug("### Effective url is : {}", composedURL);
        return composedURL;
    }
}
