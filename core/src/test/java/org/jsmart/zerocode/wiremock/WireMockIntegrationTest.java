package org.jsmart.zerocode.wiremock;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("web_app.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class WireMockIntegrationTest {

    @Test
    @JsonTestCase("wiremock_integration/mock_via_wiremock_then_test_the_end_point.json")
    public void testWireMock() throws Exception {

    }
}

