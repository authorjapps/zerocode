package org.jsmart.zerocode.core.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jsmart.zerocode.core.runner.e2e.EchoStepShortForm;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ZeroCodeMultiStepsScenarioRunner_EchoStepTest {

    @Test
    public void test_echoStepWithoutUrlOperationAndAssertionsPasses_javaCallWithoutAssertionsFails() {

        Result result = JUnitCore.runClasses(EchoStepShortForm.class);

        assertThat(result.getRunCount(), is(2));
        assertThat(result.getFailureCount(), is(1));

        Failure failure = result.getFailures().get(0);
        assertThat(failure.getDescription().getMethodName(), is("testJavaCallWithoutAssertions"));
    }
}
