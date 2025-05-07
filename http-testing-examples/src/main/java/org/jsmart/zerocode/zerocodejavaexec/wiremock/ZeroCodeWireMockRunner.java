package org.jsmart.zerocode.zerocodejavaexec.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class ZeroCodeWireMockRunner extends ZeroCodeUnitRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeWireMockRunner.class);
    static String basePath;
    static String fullPath;
    static int port = 8383;

    static WireMockServer mockServer = new WireMockServer(port);
    public ZeroCodeWireMockRunner(Class<?> klass) throws InitializationError {
        super(klass);
        simulateServerDelay();
    }


    public static void simulateServerDelay() {
        LOGGER.debug("Setting up WireMock with server delay...");

        basePath = "http://localhost:" + port;
        String path = "/delay/ids/2";
        fullPath = basePath + path;

        mockServer.start();

        mockServer.stubFor(
                get(urlEqualTo(path))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(2000)
                        ));
    }
}
