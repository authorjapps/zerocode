package org.jsmart.zerocode.testhelp.tests.helloworlddateafterbefore;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldDateAfterBeforeTest {

    @Test
    @JsonTestCase("helloworld_date/hello_world_date_after_before_test.json")
    public void testCreatedDateAfterBefore() throws Exception {

    }
}
