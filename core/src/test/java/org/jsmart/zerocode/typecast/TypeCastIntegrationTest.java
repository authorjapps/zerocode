package org.jsmart.zerocode.typecast;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("config_hosts.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class TypeCastIntegrationTest {

    @Test
    @Scenario("integration_test_files/type_cast/cast_types_to_int_bool_test.json")
    public void willPassOnlyIfCastedTo_types() throws Exception {

    }
}

