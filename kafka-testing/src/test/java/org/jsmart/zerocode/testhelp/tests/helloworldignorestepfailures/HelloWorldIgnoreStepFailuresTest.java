package org.jsmart.zerocode.testhelp.tests.helloworldignorestepfailures;

import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@TargetEnv("github_host.properties")
@RunWith(ZeroCodeUnitRunner.class)
public class HelloWorldIgnoreStepFailuresTest {

    // final outcome is Fail, but all steps executed
    @Test
    @JsonTestCase("helloworld_ignore_step_failures/ignore_step_failures_exec_all.json")
    public void testMultiStepIgnoreStepFailures_execAll() throws Exception {
    }

}
