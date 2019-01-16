package org.jsmart.zerocode.testhelp.tests.helloworldarraysize;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldArraySizeTest {

    @Test
    @JsonTestCase("helloworld_array_size/hello_world_array_n_size_assertions_test.json")
    public void testArraySize() throws Exception {

    }

}
