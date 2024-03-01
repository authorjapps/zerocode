package org.jsmart.zerocode.core.runner.retry;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@TargetEnv("dev_test.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class RetryWithStateTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryWithStateTest.class);

    static String basePath;
    static String fullPath;
    static int port = 8484;

    static WireMockServer mockServer;

    @BeforeClass
    public static void setUpWireMock() throws Exception {
        mockServer = new WireMockServer(port);
        basePath = "http://localhost:" + port;
        String path = "/retry/ids/1";
        fullPath = basePath + path;

        mockServer.start();

        mockServer.stubFor(get(urlEqualTo(path))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(500))
                .willSetStateTo("retry")
        );

        mockServer.stubFor(get(urlEqualTo(path))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("retry")
                .willReturn(aResponse()
                        .withStatus(200))
        );
    }

    @AfterClass
    public static void tearDown() {
        LOGGER.debug("##Stopping the mock server and then shutting down");
        mockServer.stop();
        mockServer.shutdown();
        LOGGER.debug("##Successfully stopped the mock server and then SHUTDOWN.");
    }

    @Test
    @JsonTestCase("integration_test_files/retry_test_cases/04_REST_retry_with_state_test.json")
    public void testRetryScenario() {
    }

    @Ignore("Only for sanity")
    @Test
    public void testRetry() throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(fullPath);

        HttpResponse response = httpClient.execute(request);
        assertThat(response.getStatusLine().getStatusCode(), is(500));

        sleep(1000);

        response = httpClient.execute(request);
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
}
