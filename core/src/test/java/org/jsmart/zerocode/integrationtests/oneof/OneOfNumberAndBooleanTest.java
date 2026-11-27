package org.jsmart.zerocode.integrationtests.oneof;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeUnitRunner.class)
public class OneOfNumberAndBooleanTest {

    @Test
    @Scenario("integration_test_files/one_of/one_of_number_and_boolean_test.json")
    public void testOneOf_numberBooleanAndText() throws Exception {
    }
}
