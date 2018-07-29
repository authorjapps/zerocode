package org.jsmart.zerocode.core.junit;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class JunitUsualWithZerocodeUnitTest {

    @Test
    public void testZello() {
        assertTrue(1 == 1);
    }

    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json.
     * @RunWith(TestOnlyZeroCodeUnitRunner.class) : starts these mocks first before running the tests
     */
    @Test
    @JsonTestCase("get_api/simple_get_api_test.json")
    public void testSimpleGetApi() throws Exception {
    
    }

}



