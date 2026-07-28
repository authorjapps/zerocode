package org.jsmart.zerocode.integrationtests.urlencoded;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeUnitRunner.class)
public class UrlEncodedTokenTest {

    @Test
    @Scenario("integration_test_files/url_encoded/url_encoded_id_in_next_step_test.json")
    public void testUrlEncodedJsonPathOfEarlierStep() throws Exception {
    }
}
