package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * See also:
 * RestEndPointMockerTest.java - for integration tests.
 *
 */
public class MockStepTest {

    private ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSerDeSer() throws Exception {
        final String aMock = SmartUtils.readJsonAsString("wiremock/test_mock_step.json");

        final MockStep mockStep = objectMapper.readValue(aMock, MockStep.class);

        assertThat(mockStep.getOperation(), is("GET"));
        assertThat(mockStep.getResponse().get("status").asInt(), is(200));
        assertThat(mockStep.getResponse().get("status").intValue(), is(200));
        assertThat(mockStep.getResponse().get("status").toString(), is("200"));

    }

    @Test
    public void testSerDeSer_headers() throws Exception {
        final String aMock = SmartUtils.readJsonAsString("wiremock/test_mock_step_request_headers.json");

        final MockStep mockStep = objectMapper.readValue(aMock, MockStep.class);

        assertThat(mockStep.getOperation(), is("GET"));
        assertThat(mockStep.getRequest().get("headers").toString(), is("{\"key\":\"key-001\",\"secret\":\"secret-001\"}"));

        HashMap<String,Object> headersMap = (HashMap<String,Object>)objectMapper.readValue(mockStep.getRequest().get("headers").toString(), HashMap.class);
        assertThat(headersMap.get("key"), is("key-001"));
        assertThat(headersMap.get("secret"), is("secret-001"));

    }

    @Test
    public void testSerDeSer_body() throws Exception {
        final String aMock = SmartUtils.readJsonAsString("wiremock/test_mock_step_request_body.json");

        final MockStep mockStep = objectMapper.readValue(aMock, MockStep.class);

        assertThat(mockStep.getOperation(), is("GET"));
        assertThat(mockStep.getRequest().get("body").toString(),
                is("{\"name\":\"Emma\",\"age\":33,\"address\":{\"line1\":\"address line 1\",\"line2\":\"address line 2\"}}"));

        // The followings are not needed as WireMock will accept the full JSON body as it is
        HashMap<String,Object> bodyAsMap = (HashMap<String,Object>)objectMapper.readValue(mockStep.getRequest().get("body").toString(), HashMap.class);
        assertThat(bodyAsMap.get("name"), is("Emma"));
        assertThat(((HashMap)bodyAsMap.get("address")).get("line1"), is("address line 1"));

    }
}