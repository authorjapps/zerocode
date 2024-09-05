package org.jsmart.zerocode.core.httpclient.oauth2;

import java.util.Map;
import java.util.Timer;

import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.http.client.methods.RequestBuilder;

/**
 * @author santhoshTpixler
 *
 */


/*
 * Note: This implementation supports the OAuth2.0 with refresh_token
 * 
 * Reference: https://tools.ietf.org/html/rfc6749#page-11
 * 
 * 
 *  1. The refresh_token, access_token URL, client_id and client_secret
 *  	should be generated by the user and stored in the properties file 
 *  	mentioned in the @TargetEnv("host.properties").
 *  2. For generating the refresh token REST Client such as Insomnia (https://insomnia.rest/) can 
 *  	be used. 
 *  
 *  Note: Postman cannot be used as it does not show the refresh token.
 */
public class OAuth2HttpClient extends BasicHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2HttpClient.class);

	/*
	 * Properties to be fetched from the host.properties
	 */
	private static final String CLIENT_ID = "client_id";
	private static final String CLIENT_SECRET = "client_secret";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String ACCOUNTS_URL = "accounts_url";
	private static final String GRANT_TYPE = "grant_type";
	/*
	 * If the Authorization header contains the replacement value as specified by the 
	 * below constant, then it is replaced with the valid access token
	 */
	private static final String ACCESS_TOKEN_REPLACEMENT_VALUE = "DIY";
	/*
	 * Time interval in which the accessToken should be renewed
	 */
	private static final long REFRESH_INTERVAL = 3540000;

	private OAuth2Impl oauth2 = null;

	@Inject
	public OAuth2HttpClient(@Named(CLIENT_ID) String clientId, @Named(CLIENT_SECRET) String clientSecret,
			@Named(REFRESH_TOKEN) String refreshToken, @Named(ACCOUNTS_URL) String accountsURL, @Named(GRANT_TYPE) String grant_type) {
		this.oauth2 = new OAuth2Impl(clientId, clientSecret, refreshToken, accountsURL, grant_type);
		if ("refresh_token".equals(grant_type)) {
			Timer timer = new Timer();
			timer.schedule(oauth2, 0, REFRESH_INTERVAL);
			synchronized (oauth2) {
				try {
					// to ensure the access token is generated before proceeding.
					oauth2.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} else {
			// This will call generateToken() internally
			oauth2.run();
		}
	}
	
	@Override
	public RequestBuilder handleHeaders(Map<String, Object> headers, RequestBuilder requestBuilder) {
		String authorization = (String) headers.get("Authorization");
		if (authorization != null && authorization.equals(ACCESS_TOKEN_REPLACEMENT_VALUE)) {
			headers.put("Authorization", oauth2.getAccessToken());
			LOGGER.info("Token injected into header.");
		}
		return super.handleHeaders(headers, requestBuilder);
	}
}
