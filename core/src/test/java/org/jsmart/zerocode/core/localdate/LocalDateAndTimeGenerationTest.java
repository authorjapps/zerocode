package org.jsmart.zerocode.core.localdate;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class LocalDateAndTimeGenerationTest {
    
    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @JsonTestCase("15_localdatetime/10_local_date_generation.json")
    public void willGetResponse_headers() throws Exception {
    
    }

}



