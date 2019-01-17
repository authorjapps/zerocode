package org.jsmart.zerocode.zerocodejavaexec.httpclient;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomHttpClient extends BasicHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpClient.class);

    public CustomHttpClient() {
        super();
        LOGGER.info("###Initialized 0 args - ");
    }

    public CustomHttpClient(CloseableHttpClient httpclient) {
        super(httpclient);
        LOGGER.info("###Initialized 1 arg - ");
    }

    /**
     * This method has been overridden here simply to show how a custom/project-specific http client
     * can be plugged into the framework.
     *
     * e.g. You can create your own project specific http client needed for http/https/tls connections or
     * a Corporate proxy based Http client here.
     * Sometimes you may need a simple default http client
     * e.g. HttpClients.createDefault() provided by Apache lib.
     *
     * Note:
     * If you do not override this method, the framework anyways creates a http client suitable for both http/https.
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

    /**
     * Overridden this method in case you want to handle the headers differently which were passed
     * from the test-case requests or you need to add any custom headers etc.
     * If not overridden, The framework will fall back to this default implementation to handle this.
     */
    @Override
    public Map<String, Object> amendRequestHeaders(Map<String, Object> headers) {
        // ----------------------------------------------------
        // Add your custom headers here(if any).
        // e.g. Your auth tokens, client_id or client_secret etc
        // ----------------------------------------------------
        if (headers != null) {
            addCustomHeaders(headers);
        } else {
            headers = new HashMap<>();
            addCustomHeaders(headers);
        }

        return headers;
    }

    private void addCustomHeaders(Map<String, Object> headers) {
        String my_value = UUID.randomUUID().toString();
        headers.put("my_key", my_value);

        String x_token_value = "secret_value_001";
        headers.put("x_token", x_token_value);

        LOGGER.info("###Added custom headers my_key={}, x_token={} to headers", my_value, x_token_value);
    }
}

