package org.jsmart.zerocode.testhelp.tests.wiremock;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("customer_web_app.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class WireMockCustomerEndPointTest {

    @Test
    @JsonTestCase("wiremock_tests/mock_via_wiremock_then_test_the_end_point.json")
    public void testHelloWorld_localhostApi() throws Exception {

    }

    @Test
    @JsonTestCase("wiremock_tests/soap_mocking_via_wiremock_test.json")
    public void testHelloWorld_soap() throws Exception {

    }

    String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <GetCurrencyRate xmlns=\"http://tempuri.org/\">\n" +
            "      <Currency>string</Currency>\n" +
            "      <RateDate>dateTime</RateDate>\n" +
            "    </GetCurrencyRate>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";
}
