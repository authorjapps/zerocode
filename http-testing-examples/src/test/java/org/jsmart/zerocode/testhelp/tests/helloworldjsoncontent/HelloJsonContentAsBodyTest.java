package org.jsmart.zerocode.testhelp.tests.helloworldjsoncontent;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloJsonContentAsBodyTest {

	@Test
	@JsonTestCase("helloworld_json_content/hello_world_json_content_as_request_body.json")
	public void testHelloWorld_jsonContentAsBody() throws Exception {
	}

}
