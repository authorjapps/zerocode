package org.jsmart.zerocode.testhelp.tests.helloworldmore;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("hello_world_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class JustHelloWorldMoreTest {

    @Test
    @JsonTestCase("helloworld_more/hello_world_ok_status_200.json")
    public void testHelloWorld_localhostApi() throws Exception {
    }

    @Test
    @JsonTestCase("helloworld_more/hello_world_post_201.json")
    public void testHelloWorld_post() throws Exception {
    }

    @Test
    @JsonTestCase("helloworld_more/hello_world_get_new_emp_200.json")
    public void testHelloWorld_getTheNewEmp() throws Exception {
    }

    @Test
    @JsonTestCase("helloworld_more/hello_world_all_integrated_apis.json")
    public void testHelloWorld_AllApi() throws Exception {
    }

    @Test
    @JsonTestCase("helloworld_more/hello_world_json_tree.json")
    public void testMoreDepthJsonBut_doesntMatter() throws Exception {
    }

}
