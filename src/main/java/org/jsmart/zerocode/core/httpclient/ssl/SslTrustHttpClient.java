package org.jsmart.zerocode.core.httpclient.ssl;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
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

public class SslTrustHttpClient extends BasicHttpClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(SslTrustHttpClient.class);

    public SslTrustHttpClient() {
        super();
    }

    public SslTrustHttpClient(CloseableHttpClient httpclient) {
        super(httpclient);
    }

    /**
     * This method has been overridden here simply to show how a custom/project-specific http client
     * can be plugged into the framework.
     *
     * e.g. You can create your own project specific http client needed for http/https/tls connections.
     * Sometimes you may not need a SSLContext, sometimes you need one, some other times you need a
     * simple default http client e.g. HttpClients.createDefault() provided by Apache.
     *
     * If you do not override this method, the framework creates a http client suitable for both http/https.
     */
    @Override
    public CloseableHttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        LOGGER.info("###Used SSL Enabled Http Client for http/https/TLS connections");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (certificate, authType) -> true).build();

        CookieStore cookieStore = new BasicCookieStore();

        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setDefaultCookieStore(cookieStore)
                .build();
    }

}

