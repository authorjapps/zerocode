package org.jsmart.zerocode.core.httpclient.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UrlQueryParamsUtilsTest {

    @Test
    public void testNotNull_queryParams() {
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("q1", "value1");
        queryParamsMap.put("q2", "value2");

        String qualifiedQueryParams = UrlQueryParamsUtils.createQualifiedQueryParams(queryParamsMap);

        assertThat(qualifiedQueryParams, is("?q1=value1&q2=value2"));
    }

    @Test
    public void testNull_queryParams() {
        Map<String, Object> queryParamsMap = new HashMap<>();
        String qualifiedQueryParams = UrlQueryParamsUtils.createQualifiedQueryParams(queryParamsMap);

        assertThat(qualifiedQueryParams, is(""));

    }

}