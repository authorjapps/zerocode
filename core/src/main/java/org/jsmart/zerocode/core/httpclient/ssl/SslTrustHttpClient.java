package org.jsmart.zerocode.core.httpclient.ssl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class SslTrustHttpClient extends BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslTrustHttpClient.class);

    /**
     * This is an optional config for the HttpClient.
     * If this is configured, then the client will implicitly wait till the configured
     * amount of time for the server response.
     * If it doesn't receive the response from the server within that time,
     * then it will throw java.net.SocketTimeoutException.
     * <p>
     * If this is not configured, then the client will continue working as usual.
     * In this case :
     * e.g. if the server takes more time to respond, the client will keep on waiting
     * till the server responds or till a network-timeout occurs.
     */
    public static final String HTTP_MAX_TIMEOUT_MILLISECONDS = "http.max.timeout.milliseconds";

    @Inject(optional = true)
    @Named(HTTP_MAX_TIMEOUT_MILLISECONDS)
    private Integer implicitWait;

    public SslTrustHttpClient() {
        super();
    }

    public SslTrustHttpClient(CloseableHttpClient httpclient) {
        super(httpclient);
    }

    /**
     * This method has been overridden here simply to show how a custom/project-specific http client
     * can be plugged into the framework.
     * <p>
     * e.g. You can create your own project specific http client needed for http/https/tls connections.
     * Sometimes you may not need a SSLContext, sometimes you need one, some other times you need a
     * simple default http client e.g. HttpClients.createDefault() provided by Apache.
     * <p>
     * If you do not override this method, the framework creates a http client suitable for both http/https.
     */
    @Override
    public CloseableHttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        LOGGER.debug("###Used SSL Enabled Http Client for http/https/TLS connections");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        RequestConfig timeOutConfig = createMaxTimeOutConfig();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(timeOutConfig)
                .build();
    }

    /**
     * Timeout Parameters:
     * 1) setConnectTimeout – The time needed to establish the connection with the remote host
     * 2) setSocketTimeout – The time needed to wait for the data after establishing the connection to remote host
     * 3) setConnectionRequestTimeout – The time needed to wait to get a connection from the connection manager/pool
     *
     * @return RequestConfig
     */
    private RequestConfig createMaxTimeOutConfig() {
        RequestConfig timeOutConfig;
        if (implicitWait == null) {
            timeOutConfig = RequestConfig.DEFAULT;
            LOGGER.debug("\n*Implicit-Wait/Connection-Timeout not configured.*" +
                            "\nE.g. to configure it for 10sec, use: '{}={}' in the host-config properties. " +
                            "\n**You can safely ignore this warning to retain the default httpClient behavior**\n",
                    HTTP_MAX_TIMEOUT_MILLISECONDS, 10000);
        } else {
            int timeout = implicitWait.intValue();
            timeOutConfig = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
            LOGGER.debug("\n----------------------------------------------------------------\n" +
                    "Implicit-Wait/Connection-Timeout config = " + implicitWait +
                    " milli-second." +
                    "\n----------------------------------------------------------------\n");
        }

        return timeOutConfig;
    }

    // Unit testing
    void setImplicitWait(Integer implicitWait) {
        this.implicitWait = implicitWait;
    }
}

