package org.jsmart.zerocode.integrationtests.jsoncontent;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeUnitRunner.class)
public class JsonContentAsItIsTest {

    @Test
    @Scenario("integration_test_files/json_content/json_content_as_it_is_test.json")
    public void testJsonContent_specialCharactersTextAndArray() throws Exception {
    }
}
