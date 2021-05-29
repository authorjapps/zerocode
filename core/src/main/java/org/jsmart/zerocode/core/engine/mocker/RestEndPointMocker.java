package org.jsmart.zerocode.core.engine.mocker;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jknack.handlebars.Helper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jsmart.zerocode.core.domain.MockStep;
import org.jsmart.zerocode.core.domain.MockSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class RestEndPointMocker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEndPointMocker.class);

    public static WireMockServer wireMockServer;

    public static Boolean shouldBuildStrictUrlMatcherForAllUrls = false;

    private static boolean hasMoreThanOneStubForSameUrlPath(List<String> urls) {
        Set<String> urlPathsSet = urls.stream()
                .map(u -> (u.contains("?")) ? u.substring(0, u.indexOf("?")) : u) // remove query params for comparison
                .collect(Collectors.toSet());
        return urlPathsSet.size() != urls.size();
    }

    public static void createWithWireMock(MockSteps mockSteps, int mockPort) {

        restartWireMock(mockPort);

        List<String> urls = mockSteps.getMocks()
                .stream()
                .map(MockStep::getUrl)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (urls.size() != 0 && hasMoreThanOneStubForSameUrlPath(urls)) {
            shouldBuildStrictUrlMatcherForAllUrls = true;
        }
        LOGGER.info("Going to build strict url matcher - {}",shouldBuildStrictUrlMatcherForAllUrls);
        mockSteps.getMocks().forEach(mockStep -> {
            JsonNode jsonNodeResponse = mockStep.getResponse();
            JsonNode jsonNodeBody = jsonNodeResponse.get("body");
            String jsonBodyRequest = (jsonNodeBody != null) ? jsonNodeBody.toString() : jsonNodeResponse.get("xmlBody").asText();


            if ("GET".equals(mockStep.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the GET endpoint");
                givenThat(createGetRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the GET endpoint -done- *****");
            } else if ("POST".equals(mockStep.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the POST endpoint");
                givenThat(createPostRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the POST endpoint -done-*****");
            } else if ("PUT".equals(mockStep.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the PUT endpoint");
                givenThat(createPutRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the PUT endpoint -done-*****");
            } else if ("PATCH".equals(mockStep.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the PATCH endpoint");
                givenThat(createPatchRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the PATCH endpoint -done-*****");
            } else if ("DELETE".equals(mockStep.getOperation())) {
                LOGGER.info("*****WireMock- Mocking the DELETE endpoint");
                givenThat(createDeleteRequestBuilder(mockStep)
                        .willReturn(responseBuilder(mockStep, jsonBodyRequest)));
                LOGGER.info("WireMock- Mocking the DELETE endpoint -done-*****");
            }

        });
    }

    public static void restartWireMock(int dynamicPort) {
        if (wireMockServer != null) {
            /*
             * Stop the wireMock server if it is running previously due to any other tests.
             */
            wireMockServer.stop();
        }
        wireMockServer = new WireMockServer(
                wireMockConfig()
                        .extensions(new ResponseTemplateTransformer(true, getWiremockHelpers()))
                        .port(dynamicPort)); // <-- Strange
        wireMockServer.start();
        WireMock.configureFor("localhost", dynamicPort); // <-- Repetition of PORT was needed, this is a wireMock bug
    }

    private static Map<String, Helper> getWiremockHelpers() {
        Map<String, Helper> helperMap = new HashedMap();
        helperMap.put("localdatetime", new HandlebarsLocalDateHelper());
        return helperMap;
    }

    public static void stopWireMockServer() {
        if (null != wireMockServer) {
            wireMockServer.stop();
            wireMockServer = null;
            LOGGER.info("Scenario: All mockings done via WireMock server. Dependant end points executed. Stopped WireMock.");
        }
    }

    private static MappingBuilder createDeleteRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = delete(buildUrlPattern(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createPatchRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = patch(buildUrlPattern(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createPutRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = put(buildUrlPattern(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createPostRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = post(buildUrlPattern(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static MappingBuilder createGetRequestBuilder(MockStep mockStep) {
        final MappingBuilder requestBuilder = get(buildUrlPattern(mockStep.getUrl()));
        return createRequestBuilderWithHeaders(mockStep, requestBuilder);
    }

    private static UrlPattern buildUrlPattern(String url) {
        // if url pattern doesn't have query params and shouldBuildStrictUrlMatcher is true, then match url regardless query parameters
        if (url != null && !url.contains("?") && !shouldBuildStrictUrlMatcherForAllUrls) {
            LOGGER.info("Going to build lenient matcher for url={}",url);
            return urlPathEqualTo(url);
        } else { // if url pattern has query params then match url strictly including query params
            LOGGER.info("Going to build strict matcher for url={}",url);
            return urlEqualTo(url);
        }
    }

    private static MappingBuilder createRequestBuilderWithHeaders(MockStep mockStep, MappingBuilder requestBuilder) {

        final String bodyJson = mockStep.getBody();
        // -----------------------------------------------
        // read request body and set to request builder
        // -----------------------------------------------
        if (StringUtils.isNotEmpty(bodyJson)) {
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
        ResponseDefinitionBuilder responseBuilder = aResponse()
                .withStatus(mockStep.getResponse().get("status").asInt());
        JsonNode headers = mockStep.getResponse().get("headers");
        JsonNode contentType = headers != null ? headers.get("Content-Type") : null;
        responseBuilder = contentType != null ?
                responseBuilder.withHeader("Content-Type", contentType.textValue()).withBody(jsonBodyRequest) :
                responseBuilder.withBody(jsonBodyRequest);

        return responseBuilder;
    }

    public static int createWithLocalMock(String endPointJsonApi) {
        if (StringUtils.isNotEmpty(endPointJsonApi)) {
            // read this json into virtuoso.
        }

        return 200;
    }

    public static WireMockServer getWireMockServer() {
        return wireMockServer;
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

}
