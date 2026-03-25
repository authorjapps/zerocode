package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for MockStep deserialization.
 * MockStep is used by $USE.SIMULATOR and $USE.VIRTUOSO test patterns.
 */
public class MockStepTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapperProvider().get();
    }

    @Test
    public void testDeserialize_operationAndResponse() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Mock GET person\",\n" +
                "  \"operation\": \"GET\",\n" +
                "  \"url\": \"/api/persons/1\",\n" +
                "  \"response\": {\n" +
                "    \"status\": 200,\n" +
                "    \"body\": {\n" +
                "      \"id\": \"p001\",\n" +
                "      \"name\": \"Emma\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        MockStep mockStep = objectMapper.readValue(json, MockStep.class);

        assertThat(mockStep.getName(), is("Mock GET person"));
        assertThat(mockStep.getOperation(), is("GET"));
        assertThat(mockStep.getUrl(), is("/api/persons/1"));
        assertThat(mockStep.getResponse().get("status").asInt(), is(200));
        assertThat(mockStep.getResponse().get("body").get("id").asText(), is("p001"));
        assertThat(mockStep.getResponse().get("body").get("name").asText(), is("Emma"));
    }

    @Test
    public void testDeserialize_requestHeaders() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Mock GET with headers\",\n" +
                "  \"operation\": \"GET\",\n" +
                "  \"url\": \"/api/persons/1\",\n" +
                "  \"request\": {\n" +
                "    \"headers\": {\n" +
                "      \"key\": \"key-007\",\n" +
                "      \"secret\": \"secret-007\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"response\": {\n" +
                "    \"status\": 200\n" +
                "  }\n" +
                "}";

        MockStep mockStep = objectMapper.readValue(json, MockStep.class);

        assertThat(mockStep.getOperation(), is("GET"));
        assertThat(mockStep.getRequest().get("headers").get("key").asText(), is("key-007"));
        assertThat(mockStep.getRequest().get("headers").get("secret").asText(), is("secret-007"));

        Map<String, Object> headersMap = mockStep.getHeadersMap();
        assertThat(headersMap.get("key"), is("key-007"));
        assertThat(headersMap.get("secret"), is("secret-007"));
    }

    @Test
    public void testDeserialize_requestBody() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Mock POST person\",\n" +
                "  \"operation\": \"POST\",\n" +
                "  \"url\": \"/api/persons\",\n" +
                "  \"request\": {\n" +
                "    \"body\": {\n" +
                "      \"name\": \"Emma\",\n" +
                "      \"age\": 33\n" +
                "    }\n" +
                "  },\n" +
                "  \"response\": {\n" +
                "    \"status\": 201,\n" +
                "    \"body\": {\n" +
                "      \"id\": \"p001\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        MockStep mockStep = objectMapper.readValue(json, MockStep.class);

        assertThat(mockStep.getOperation(), is("POST"));
        assertThat(mockStep.getResponse().get("status").asInt(), is(201));
        assertThat(mockStep.getBody(), is("{\"name\":\"Emma\",\"age\":33}"));
        assertThat(mockStep.getHeadersMap().isEmpty(), is(true));
    }

    @Test
    public void testDeserialize_noRequestMeansNullBody() throws Exception {
        String json = "{\n" +
                "  \"name\": \"Mock DELETE\",\n" +
                "  \"operation\": \"DELETE\",\n" +
                "  \"url\": \"/api/persons/1\",\n" +
                "  \"response\": {\n" +
                "    \"status\": 204\n" +
                "  }\n" +
                "}";

        MockStep mockStep = objectMapper.readValue(json, MockStep.class);

        assertThat(mockStep.getOperation(), is("DELETE"));
        assertThat(mockStep.getResponse().get("status").asInt(), is(204));
        assertThat(mockStep.getBody(), nullValue());
        assertThat(mockStep.getHeadersMap().isEmpty(), is(true));
    }
}
