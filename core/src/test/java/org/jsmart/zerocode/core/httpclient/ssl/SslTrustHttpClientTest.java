package org.jsmart.zerocode.core.httpclient.ssl;

import com.sun.net.httpserver.HttpServer;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for SslTrustHttpClient's implicit wait / timeout configuration.
 *
 * Uses a lightweight JDK HttpServer (no extra dependencies) that introduces
 * a fixed 2000ms delay before responding, so we can verify that:
 *  - a wait configured higher than the delay succeeds
 *  - a wait configured lower than the delay times out
 *  - no wait configured means no timeout (default behaviour)
 */
public class SslTrustHttpClientTest {

    static final int PORT = 9191;
    static final int SERVER_DELAY_MS = 2000;
    static final int WAIT_HIGHER_THAN_DELAY = 3000;
    static final int WAIT_LOWER_THAN_DELAY = 1000;

    static HttpServer slowServer;
    static String targetUrl;

    @BeforeClass
    public static void startSlowServer() throws Exception {
        slowServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        slowServer.createContext("/delay/test", exchange -> {
            try {
                Thread.sleep(SERVER_DELAY_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException("Sleep failed in Unit Testing:" + e);
            }
            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();
        });
        slowServer.start();
        targetUrl = "http://localhost:" + PORT + "/delay/test";
    }

    @AfterClass
    public static void stopSlowServer() {
        if (slowServer != null) {
            slowServer.stop(0);
        }
    }

    /**
     * implicitWait (3000ms) > server delay (2000ms) → request completes successfully.
     */
    @Test
    public void testImplicitDelay_waitHigherThanDelay() throws Exception {
        SslTrustHttpClient client = new SslTrustHttpClient();
        client.setImplicitWait(WAIT_HIGHER_THAN_DELAY);

        CloseableHttpClient httpClient = client.createHttpClient();
        org.apache.http.HttpResponse response = httpClient.execute(new HttpGet(targetUrl));

        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }

    /**
     * implicitWait (1000ms) < server delay (2000ms) → SocketTimeoutException.
     */
    @Test(expected = SocketTimeoutException.class)
    public void testImplicitDelay_timeoutWhenWaitLowerThanDelay() throws Exception {
        SslTrustHttpClient client = new SslTrustHttpClient();
        client.setImplicitWait(WAIT_LOWER_THAN_DELAY);

        CloseableHttpClient httpClient = client.createHttpClient();
        httpClient.execute(new HttpGet(targetUrl));
    }

    /**
     * No implicitWait configured → RequestConfig.DEFAULT → no timeout,
     * request waits as long as needed and completes successfully.
     */
    @Test
    public void testImplicitDelay_noConfigMeansNoTimeout() throws Exception {
        SslTrustHttpClient client = new SslTrustHttpClient();
        // intentionally no setImplicitWait call

        CloseableHttpClient httpClient = client.createHttpClient();
        org.apache.http.HttpResponse response = httpClient.execute(new HttpGet(targetUrl));

        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
}
