package org.jsmart.zerocode.core.runner;

import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.tests.customrunner.TestOnlyZeroCodeUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@HostProperties(host="http://localhost", port=9998, context = "")
@RunWith(TestOnlyZeroCodeUnitRunner.class)
public class ZeroCodeMultiStepsScenarioRunnerImplVerifyTest {
    
    /**
     * Mock end points are in test/resources: simulators/test_purpose_end_points.json
     */

    @Test
    @JsonTestCase("13_headers/10_response_with_headers.json")
    public void willGetResponse_headers() throws Exception {
    
    }
    
    @Test
    @JsonTestCase("01_verification_test_cases/10_text_node_json_test.json")
    public void willPassIfFound_textJsonNode() throws Exception {
        
    }
    
    @Test
    @JsonTestCase("01_verification_test_cases/15_non_json_string_body_test.json")
    public void willPassIfFound_nonJsonRawBody() throws Exception {
        
    }
    
    @Test
    @JsonTestCase("01_verification_test_cases/20_non_json_assert_part_string.json")
    public void willAssertNonJson_usingPlaceHolder() throws Exception {
        
    }
    
    @Ignore
    @Test
    @JsonTestCase("01_verification_test_cases/20_ignored.json")
    public void willNotRunIf_ignored() throws Exception {
        
    }
}



