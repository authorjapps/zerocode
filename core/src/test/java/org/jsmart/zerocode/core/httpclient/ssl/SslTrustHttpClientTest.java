package org.jsmart.zerocode.core.httpclient.ssl;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SslTrustHttpClientTest {

    @Mock
    CloseableHttpClient httpClient;

    @InjectMocks
    SslTrustHttpClient sslTrustHttpClient;

    static String basePath;
    static String fullPath;
    static int port = 8383;

    static WireMockServer mockServer = new WireMockServer(port);

    public static final int SERVER_DELAY = 2000;
    public static final int MAX_IMPLICIT_WAIT_HIGH = 3000;
    public static final int MAX_IMPLICIT_WAIT_LOW = 1500;

    @BeforeClass
    public static void setUpWireMock() throws Exception {
        basePath = "http://localhost:" + port;
        String path = "/delay/ids/1";
        fullPath = basePath + path;

        mockServer.start();

        mockServer.stubFor(
                get(urlEqualTo(path))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(SERVER_DELAY)
                        ));

    }

    @AfterClass
    public static void tearDown() {
        mockServer.shutdown();
    }

    @Ignore("TODO-- unit test. Already Covered in the integration tests")
    @Test
    public void testNulPointerNotThrown_emptyBody() throws Exception {
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        HttpUriRequest mockHttpUriRequest = mock(HttpUriRequest.class);
        when(httpClient.execute(any())).thenReturn(mockResponse);
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

    @Test
    public void testImplicitDelay() throws Exception {
        SslTrustHttpClient sslTrustHttpClient = new SslTrustHttpClient();
        sslTrustHttpClient.setImplicitWait(MAX_IMPLICIT_WAIT_HIGH); //Ok - More than the implicit

        CloseableHttpClient httpClient = sslTrustHttpClient.createHttpClient();

        HttpGet request = new HttpGet(fullPath);
        HttpResponse response = httpClient.execute(request);

        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }

    @Test(expected = SocketTimeoutException.class)
    public void testImplicitDelay_timeout() throws Exception {
        SslTrustHttpClient sslTrustHttpClient = new SslTrustHttpClient();
        sslTrustHttpClient.setImplicitWait(MAX_IMPLICIT_WAIT_LOW); //Timeout - Less than the implicit

        CloseableHttpClient httpClient = sslTrustHttpClient.createHttpClient();

        HttpGet request = new HttpGet(fullPath);
        HttpResponse response = httpClient.execute(request);
    }

    @Test
    public void testImplicitDelay_noConfig() throws Exception {
        SslTrustHttpClient sslTrustHttpClient = new SslTrustHttpClient();
        //sslTrustHttpClient.setImplicitWait(none); //not configured

        CloseableHttpClient httpClient = sslTrustHttpClient.createHttpClient();

        HttpGet request = new HttpGet(fullPath);
        HttpResponse response = httpClient.execute(request);
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
}