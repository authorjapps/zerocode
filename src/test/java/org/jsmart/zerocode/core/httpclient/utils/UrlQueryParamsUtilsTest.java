package org.jsmart.zerocode.core.httpclient.utils;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UrlQueryParamsUtilsTest {

    @Test
    public void testNotNull_queryParams() throws IOException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("q1", "value1");
        queryParamsMap.put("q2", 3);
        queryParamsMap.put("q3", 1.9);

        String qualifiedQueryParams = UrlQueryParamsUtils.createQualifiedQueryParams(queryParamsMap);

        assertThat(qualifiedQueryParams, is("q1=value1&q2=3&q3=1.9"));
    }

    @Test
    public void testEmpty_queryParams() throws IOException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        String qualifiedQueryParams = UrlQueryParamsUtils.createQualifiedQueryParams(queryParamsMap);
        assertThat(qualifiedQueryParams, is(""));
    }

    @Test
    public void testNull_queryParams() throws IOException {
        Map<String, Object> queryParamsMap = null;
        String qualifiedQueryParams = UrlQueryParamsUtils.createQualifiedQueryParams(queryParamsMap);
        assertThat(qualifiedQueryParams, is(""));
    }

    @Test
    public void testQueryParams_frontSlash() throws IOException {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("state/region", "singapore north");
        queryParamsMap.put("q2", "value2");

        String qualifiedQueryParams = UrlQueryParamsUtils.createQualifiedQueryParams(queryParamsMap);

        assertThat(qualifiedQueryParams, is("q2=value2&state%2Fregion=singapore+north"));
    }

}