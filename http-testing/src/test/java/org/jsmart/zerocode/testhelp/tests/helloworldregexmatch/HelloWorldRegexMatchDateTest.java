package org.jsmart.zerocode.testhelp.tests.helloworldregexmatch;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldRegexMatchDateTest {


    @Test
    @JsonTestCase("helloworld_regex_match/hello_world_matches_string_regex_test.json")
    public void testRegexStringMatch() throws Exception {

    }

}
