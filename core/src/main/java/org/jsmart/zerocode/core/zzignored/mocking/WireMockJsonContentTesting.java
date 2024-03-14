package org.jsmart.zerocode.core.zzignored.mocking;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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

        givenThat(WireMock.get(urlEqualTo("/id-services/id-services/person/id/p_id_009/bio/default"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody(jsonBodyRequest)));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9073/id-services/id-services/person/id/p_id_009/bio/default");
        Response response = target.request().get();
        final String respBodyAsString = response.readEntity(String.class);
        JSONAssert.assertEquals(jsonBodyRequest, respBodyAsString, true);

        System.out.println("### bio response from mapping: \n" + respBodyAsString);
    }
}