package org.jsmart.zerocode.core.httpclient.ssl;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.HashMap;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SslTrustHttpClientTest {

    @Mock
    CloseableHttpClient httpClient;

    @InjectMocks
    SslTrustHttpClient sslTrustHttpClient;

    @Ignore("TODO-- unit test. Already Covered in the integration tests")
    @Test
    public void testNulPointerNotThrown_emptyBody() throws Exception {
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        HttpUriRequest mockHttpUriRequest = mock(HttpUriRequest.class);
        when(httpClient.execute(anyObject())).thenReturn(mockResponse);
        //when(requestBuilder.build()).thenReturn(mockHttpUriRequest);

        //        when(httpClient.execute(anyString(), anyString(), anyMap(), anyMap(), anyObject()))
        //                .thenReturn(mockResponse);
        Response actualResponse = sslTrustHttpClient.execute("url",
                "GET",
                new HashMap<String, Object>(),
                new HashMap<String, Object>(),
                "aBody");
        System.out.println("" + actualResponse);
    }


}