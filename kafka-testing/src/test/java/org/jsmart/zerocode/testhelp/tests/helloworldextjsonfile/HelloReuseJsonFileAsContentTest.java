package org.jsmart.zerocode.testhelp.tests.helloworldextjsonfile;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloReuseJsonFileAsContentTest {

    @Test
    @JsonTestCase("helloworld_ext_file_json/hello_world_jsonfile_as_request_body.json")
    public void testHelloWorld_jsonFileAsBody() throws Exception {
    }

    @Test
    @JsonTestCase("helloworld_ext_file_json/hello_world_jsonfile_as_response_body.json")
    public void testHello_jsonFileAsResponseBody() throws Exception {
    }

    @Test
    @JsonTestCase("helloworld_ext_file_json/hello_world_jsonfile_as_part_payload.json")
    public void testHello_jsonFileAsPartPayload() throws Exception {
    }

}
