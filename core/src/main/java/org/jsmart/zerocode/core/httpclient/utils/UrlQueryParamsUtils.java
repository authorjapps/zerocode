package org.jsmart.zerocode.core.httpclient.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class UrlQueryParamsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlQueryParamsUtils.class);

    public static String setQueryParams(String httpUrl, Map<String, Object> queryParams) throws IOException {
        String qualifiedQueryParams = createQualifiedQueryParams(queryParams);
        httpUrl = httpUrl + "?" + qualifiedQueryParams;

        LOGGER.info("### Effective url is : " + httpUrl);
        return httpUrl;
    }

    protected static String createQualifiedQueryParams(Map<String, Object> queryParamsMap) throws IOException {
        queryParamsMap = ofNullable(queryParamsMap).orElse(new HashMap<>());
        List<NameValuePair> nameValueList = new ArrayList<>();
        for(String key : queryParamsMap.keySet()) {
            nameValueList.add(new BasicNameValuePair(key, queryParamsMap.get(key).toString()));
        }
        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValueList);
        String qualifiedQueryParam = EntityUtils.toString(httpEntity, "UTF-8");

        LOGGER.info("### qualifiedQueryParams : " + qualifiedQueryParam);
        return qualifiedQueryParam;
    }

}
