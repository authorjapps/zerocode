package org.jsmart.zerocode.core.engine.mocker;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.domain.MockStep;
import org.jsmart.zerocode.core.domain.MockSteps;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class RestEndPointMocker {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonServiceExecutorImpl.class);

    public static WireMockServer wireMockServer;

    public static void createWithWireMock(MockSteps mockSteps, int mockPort) {

        restartWireMock(mockPort);

        mockSteps.getMocks().forEach(mockStep -> {
            String jsonBodyRequest = mockStep.getResponse().get("body").toString();

            if("GET".equals(mockStep.getOperation())){
                LOGGER.info("*****WireMock- Mocking the GET endpoint");
                givenThat(createGetRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the GET endpoint -done- *****");
            }
            else if("POST".equals(mockStep.getOperation())){
                LOGGER.info("*****WireMock- Mocking the POST endpoint");
                givenThat(createPostRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the POST endpoint -done-*****");
            }
            else if("PUT".equals(mockStep.getOperation())){
                LOGGER.info("*****WireMock- Mocking the PUT endpoint");
                givenThat(createPutRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the PUT endpoint -done-*****");
            }
        });
    }


    public static void restartWireMock(int dynamicPort) {
        wireMockServer = new WireMockServer(wireMockConfig().port(dynamicPort)); // <-- Strange
        /*
         * Stop the wireMock server if it is running previously due to any other tests.
         */
        wireMockServer.stop();
        wireMockServer.start();
        WireMock.configureFor("localhost", dynamicPort); // <-- Repetition of PORT was needed, this is a wireMock bug
    }

    private static MappingBuilder createPutRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = put(urlEqualTo(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createPostRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = post(urlEqualTo(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createGetRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = get(urlEqualTo(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createRequestBuilderWithHeaders(MockStep mockStep, MappingBuilder requestBuilder) {

        final String bodyJson = mockStep.getBody();
        // -----------------------------------------------
        // read request body and set to request builder
        // -----------------------------------------------
        if(StringUtils.isNotEmpty(bodyJson)){
            requestBuilder.withRequestBody(equalToJson(bodyJson));
        }

        final Map<String, Object> headersMap = mockStep.getHeadersMap();
        // -----------------------------------------------
        // read request headers and set to request builder
        // -----------------------------------------------
        if (headersMap.size() > 0) {
            for (Object key : headersMap.keySet()) {
                requestBuilder.withHeader((String) key, equalTo((String) headersMap.get(key)));
            }
        }
        return requestBuilder;
    }

    private static ResponseDefinitionBuilder responseBuilder(MockStep mockStep, String jsonBodyRequest) {
        final ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(mockStep.getResponse().get("status").asInt())
                //.withHeader("Content-Type", APPLICATION_JSON) //Can be XMl also for SOAP end points
                .withBody(jsonBodyRequest);

        return responseBuilder;
    }


    /*
     * This is working code, whenever you put the virtuoso dependency here, you can uncomment this block.
     */
    public static int createWithVirtuosoMock(String endPointJsonApi) {
//        if(StringUtils.isNotEmpty(endPointJsonApi)){
//            ApiSpec apiSpec = SimulatorJsonUtils.deserialize(endPointJsonApi);
//            apiSpec.getApis().stream()
//                    .forEach(api -> {
//                        int status = aVirtuosoRestMocker()
//                                .url(api.getUrl())
//                                .operation(api.getOperation())
//                                .willReturn(
//                                        aResponse()
//                                                .status(api.getResponse().getStatus())
//                                                .body(api.getResponse().getBody())
//                                                .build()
//                                );
//
//                        if(200 != status){
//                            logbuilder.info("Mocking virtuoso end point failed. Status: " + status);
//                            throw new RuntimeException("Mocking virtuoso end point failed. Status: " + status + ". Check tunnel etc.");
//                        }
//                    });
//        }

        return 200;
    }

    public static int createWithLocalMock(String endPointJsonApi) {
        if(StringUtils.isNotEmpty(endPointJsonApi)){
            // read this json into virtuoso.
        }

        return 200;
    }
}