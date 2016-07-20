package org.jsmart.zerocode.core.runner;

import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.engine.assertion.AssertionReport;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StepNotificationHandler {
    private static final Logger logger = LoggerFactory.getLogger(StepNotificationHandler.class);

    Boolean handleAssertionFailed(RunNotifier notifier,
                                  Description description,
                                  String scenarioName,
                                  String stepName,
                                  List<AssertionReport> failureReportList){
        // Generate error report and display stating which expectation(s) did not match
        logger.info(String.format("Failed assertion during Scenario:%s, --> Step:%s, Details: %s",
                scenarioName, stepName, StringUtils.join(failureReportList, "\n")));
        notifier.fireTestFailure(new Failure(description, new RuntimeException(
                String.format( "Assertion failed for [%s]: step--> [%s], details:%n%s%n", scenarioName, stepName, StringUtils.join(failureReportList, "\n"))
        )));

        return false;
    }
    Boolean handleStepException(RunNotifier notifier,
                                Description description,
                                String scenarioName,
                                String stepName,
                                Exception stepException){
        logger.info(String.format("Exception occurred while executing Scenario:[%s], --> Step:[%s], Details: %s",
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
