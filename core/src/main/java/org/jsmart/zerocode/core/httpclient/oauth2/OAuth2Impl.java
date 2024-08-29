package org.jsmart.zerocode.core.httpclient.oauth2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author santhoshTpixler
 *
 */

/*
 * Note: This implementation supports the OAuth2.0 with refresh_token
 * 
 * Reference: https://tools.ietf.org/html/rfc6749#page-11
 */
public class OAuth2Impl extends TimerTask {
	private String clienId;
	private String clientSecret;
	private String refreshToken;
	private String accessTokenURL;

	private String accessToken;
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Impl.class);

	public OAuth2Impl(String clientId, String clientSecret, String refreshToken, String accountsUrl) {
		this.clienId = clientId;
		this.clientSecret = clientSecret;
		this.refreshToken = refreshToken;
		this.accessTokenURL = accountsUrl;
	}

	@Override
	public void run() {
		generateToken();
	}

	public synchronized String getAccessToken() {
		return accessToken;

	}

	private synchronized void setAccessToken(String token) {
		this.accessToken = "Bearer " + token;
	}

	/**
	 * Makes a POST request to the accessTokenURL to fetch the accesstoken
	 */
	private synchronized void generateToken() {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			
			
			StringBuilder URL = new StringBuilder(accessTokenURL);
			URL.append('?');
			URL.append("refresh_token=" + refreshToken);
			URL.append("&client_id=" + clienId);;
			URL.append("&client_secret=" + clientSecret);
			URL.append("&grant_type=refresh_token");
			HttpPost post = new HttpPost(URL.toString());
			
			
			/*
			 * Below code was not compatible with simulator. In production kindly make 
			 * use of the below code.
			 */
			
			/*
			 * List<NameValuePair> nameValuePairs = new ArrayList<>(4);
			 * nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
			 * nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
			 * nameValuePairs.add(new BasicNameValuePair("client_id", clienId));
			 * nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
			 * post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 */
			
			JSONObject jsonRespone = null;
			try (CloseableHttpResponse response = client.execute(post);) {
				try (InputStream stream = response.getEntity().getContent()) {
					jsonRespone = new JSONObject(new JSONTokener(stream));
				}
			}
			if (accessToken == null) {
				setAccessToken(jsonRespone.getString("access_token"));
				this.notifyAll();
			} else {
				setAccessToken(jsonRespone.getString("access_token"));
			}
		} catch (Exception e) {
			LOGGER.error("Cannot fetch access token from IAM", e);
		}

	}

}
