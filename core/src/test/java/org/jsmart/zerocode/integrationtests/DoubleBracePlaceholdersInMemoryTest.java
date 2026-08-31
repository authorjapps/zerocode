package org.jsmart.zerocode.integrationtests;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@TargetEnv("dev_test.properties")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class DoubleBracePlaceholdersInMemoryTest {

    @Test
    @Scenario("integration_test_files/placeholders/double_brace_placeholders_in_memory.json")
    public void resolvesDoubleBraceAndMixedPlaceholders() throws Exception {
    }
}
