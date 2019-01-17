package org.jsmart.zerocode.core.zzignored.mocking;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class WireMockJsonContentTesting {
    @Rule
    public WireMockRule rule = new WireMockRule(9073);

    @Test
    public void bioViaJson() throws Exception{
        String jsonBodyRequest = "{\n" +
                "    \"id\": \"303021\",\n" +
                "    \"names\": [\n" +
                "        {\n" +
                "            \"firstName\": \"You First\",\n" +
                "            \"lastName\": \"Me Last\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        givenThat(WireMock.get(urlEqualTo("/identitymanagement-services/identitymanagement-services/person/internalHandle/person_id_009/biographics/default"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(jsonBodyRequest)));

        ApacheHttpClientExecutor httpClientExecutor = new ApacheHttpClientExecutor();
        ClientRequest clientExecutor = httpClientExecutor.createRequest("http://localhost:9073/identitymanagement-services/identitymanagement-services/person/internalHandle/person_id_009/biographics/default");
        clientExecutor.setHttpMethod("GET");
        ClientResponse serverResponse = clientExecutor.execute();

        final String respBodyAsString = (String)serverResponse.getEntity(String.class);
        JSONAssert.assertEquals(jsonBodyRequest, respBodyAsString, true);

        System.out.println("### bio response from mapping: \n" + respBodyAsString);
    }
}