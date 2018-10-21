package org.jsmart.zerocode.core.httpclient;

import org.apache.http.client.methods.RequestBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BasicHttpClientTest {

    @Test
    public void createRequestBuilder() throws IOException {
        BasicHttpClient basicHttpClient = new BasicHttpClient();
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        String reqBodyAsString = "{\"Name\":\"Larry Pg\",\"Company\":\"Amazon\",\"Title\":\"CEO\"}";
        RequestBuilder requestBuilder = basicHttpClient.createRequestBuilder("/api/v1/founder", "POST", header, reqBodyAsString);
        Assert.assertEquals("POST", requestBuilder.getMethod());
    }
}