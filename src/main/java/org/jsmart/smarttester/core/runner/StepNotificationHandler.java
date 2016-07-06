package org.jsmart.smarttester.core.runner;

import org.apache.commons.lang.StringUtils;
import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StepNotificationHandler {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ZeroCodePackageRunner.class);

    Boolean handleAssertionFailed(RunNotifier notifier,
                                  Description description,
                                  String scenarioName,
                                  String stepName,
                                  List<AssertionReport> failureReportList){
        // Generate error report and display in the console stating which expectation(s) did not match
        logger.info(String.format("Failed assertion during Scenario:%s, --> Step:%s, Details: %s",
                scenarioName, stepName, StringUtils.join(failureReportList, "\n")));
        notifier.fireTestFailure(new Failure(description, new RuntimeException(
                String.format( "Assertion failed for step %s, details:%n%s%n", stepName, StringUtils.join(failureReportList, "\n"))
        )));

        return false;
    }
    Boolean handleStepException(RunNotifier notifier,
                                Description description,
                                String scenarioName,
                                String stepName,
                                Exception stepException){
        logger.info(String.format("Exception occurred while executing Scenario: %s, Step: %s, Details: %s",
                scenarioName, stepName, stepException));
        notifier.fireTestFailure(new Failure(description, stepException));

        return false;
    }
    Boolean handleAssertionPassed(RunNotifier notifier,
                                  Description description,
                                  String scenarioName,
                                  String stepName,
                                  List<AssertionReport> failureReportList){
        logger.info(String.format("\n***Step PASSED:%s->%s", scenarioName, stepName));

        return true;
    }

    public <A, B, C, D, E, R> R handleAssertion(A var1,
                                             B var2,
                                             C var3,
                                             D var4,
                                             E var5,
                                             Notifier<A, B, C, D, E, R> notifyFunc){

        R result = notifyFunc.apply(var1, var2, var3, var4, var5);

        return result;
    }

}
