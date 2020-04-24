package org.jsmart.zerocode.core.httpclient.utils;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UrlQueryParamsUtilsTest {

    public static final String BASE_URL = "http://localhost";

    @Test
    public void testNotNull_queryParams() throws URISyntaxException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("q1", "value1");
        queryParamsMap.put("q2", 3);
        queryParamsMap.put("q3", 1.9);

        String qualifiedQueryParams = UrlQueryParamsUtils.setQueryParams(BASE_URL, queryParamsMap);

        assertThat(qualifiedQueryParams, is(BASE_URL + "?q1=value1&q2=3&q3=1.9"));
    }

    @Test
    public void testEmpty_queryParams() throws URISyntaxException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        String qualifiedQueryParams = UrlQueryParamsUtils.setQueryParams(BASE_URL, queryParamsMap);
        assertThat(qualifiedQueryParams, is(BASE_URL));
    }

    @Test
    public void testNull_queryParams() throws URISyntaxException {
        Map<String, Object> queryParamsMap = null;
        String qualifiedQueryParams = UrlQueryParamsUtils.setQueryParams(BASE_URL, queryParamsMap);
        assertThat(qualifiedQueryParams, is(BASE_URL));
    }

    @Test
    public void testQueryParams_frontSlash() throws URISyntaxException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("state/region", "singapore north");
        queryParamsMap.put("q2", "value2");

        String qualifiedQueryParams = UrlQueryParamsUtils.setQueryParams(BASE_URL, queryParamsMap);

        assertThat(qualifiedQueryParams, is(BASE_URL + "?q2=value2&state%2Fregion=singapore+north"));
    }

    @Test
    public void testQueryParamsCombinedWithUri() throws URISyntaxException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("q2", "2");
        String uriWithParams = BASE_URL + "?q1=1";

        String qualifiedQueryParams = UrlQueryParamsUtils.setQueryParams(uriWithParams, queryParamsMap);
        assertThat(qualifiedQueryParams, is(BASE_URL + "?q1=1&q2=2"));
    }
}