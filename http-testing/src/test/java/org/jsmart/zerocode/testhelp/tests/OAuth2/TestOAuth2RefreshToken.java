package org.jsmart.zerocode.testhelp.tests.OAuth2;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.httpclient.oauth2.OAuth2HttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *  Run this file For testing refresh tokens only.
 * Provide essential values in host.properties file.
 */
@TargetEnv("host.properties")
@RunWith(ZeroCodeUnitRunner.class)
@UseHttpClient(OAuth2HttpClient.class)
public class TestOAuth2RefreshToken {

	// First run this Server for OAuth2 access_token_url be available
	// --> RunMeFirstLocalMockRESTServer main()
	@Test
	@Scenario("helloworld_OAuth2/OAuth_supported_request_refresh_token.json")
	public void testRefreshTokenFlow() {
		// This test will use the refresh token flow
	}

}
