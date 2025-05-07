package org.jsmart.zerocode.testhelp.tests.helloworldjavaexec;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("myapp_proto.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldJavaApiAsProtocolTest {

    @Test
    @JsonTestCase("helloworldjavaexec/hello_world_protocol_method.json")
    public void testJava_protocol() throws Exception {

    }


}
