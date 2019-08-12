package org.jsmart.zerocode.core.javamethod;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeUnitRunner.class)
public class JavaMethodExecTest {

    @Test
    @JsonTestCase("unit_test_files/java_apis/02_test_json_java_service_method_no_param.json")
    public void testJavaMethod_noParams() throws Exception {
    }
}

