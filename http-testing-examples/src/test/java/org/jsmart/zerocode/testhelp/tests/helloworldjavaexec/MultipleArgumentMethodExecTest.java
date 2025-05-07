package org.jsmart.zerocode.testhelp.tests.helloworldjavaexec;

import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeUnitRunner.class)
public class MultipleArgumentMethodExecTest {

    @Test
    @Scenario("helloworldjavaexec/java_method_multiple_arguments_test.json")
    public void test_multi_arg_method_exec() throws Exception {

    }
}
