package org.jsmart.zerocode.core.runner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsmart.zerocode.core.engine.assertion.AssertionReport;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.ofNullable;

public class StepNotificationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(StepNotificationHandler.class);
    private final int MAX_LINE_LENGTH = 130;
    
    Boolean handleAssertionFailed(RunNotifier notifier,
                    Description description,
                    String scenarioName,
                    String stepName,
                    List<AssertionReport> failureReportList) {
        /**
         * Generate error report and display clearly which expectation(s) did not match
         */
        LOGGER.error(String.format("Failed assertion during Scenario:%s, --> Step:%s, Details: %s\n",
                        scenarioName, stepName, StringUtils.join(failureReportList, "\n")));
        String prettyFailureMessage =
                String.format("Assertion failed for :- \n\n[%s] \n\t|\n\t|\n\t+---Step --> [%s] \n\nFailures:\n--------- %n%s%n",
                scenarioName,
                stepName,
                StringUtils.join(failureReportList, "\n" + deckedUpLine(maxEntryLengthOf(failureReportList)) + "\n"));
        LOGGER.error(prettyFailureMessage + "(See below 'Actual Vs Expected' to learn why this step failed) \n");
        notifier.fireTestFailure(new Failure(description, new RuntimeException( prettyFailureMessage)));
        
        return false;
    }
    
    Boolean handleStepException(RunNotifier notifier,
                    Description description,
                    String scenarioName,
                    String stepName,
                    Exception stepException) {
        LOGGER.info(String.format("Exception occurred while executing Scenario:[%s], --> Step:[%s], Details: %s",
                        scenarioName, stepName, stepException));
        notifier.fireTestFailure(new Failure(description, stepException));
        
        return false;
    }
    
    Boolean handleAssertionPassed(RunNotifier notifier,
                    Description description,
                    String scenarioName,
                    String stepName,
                    List<AssertionReport> failureReportList) {
        LOGGER.info(String.format("\n***Step PASSED:%s->%s", scenarioName, stepName));
        
        return true;
    }
    
    public <A, B, C, D, E, R> R handleAssertion(A var1,
                    B var2,
                    C var3,
                    D var4,
                    E var5,
                    Notifier<A, B, C, D, E, R> notifyFunc) {
        
        R result = notifyFunc.apply(var1, var2, var3, var4, var5);
        
        return result;
    }
    
    /**
     * all private functions below
     */
    
    private int maxEntryLengthOf(List<AssertionReport> failureReportList) {
        final Integer maxLength = ofNullable(failureReportList).orElse(Collections.emptyList()).stream()
                        .map(report -> report.toString().length())
                        .max(Comparator.naturalOrder())
                        //.min(Comparator.naturalOrder())
                        .get();
        return maxLength > MAX_LINE_LENGTH ? MAX_LINE_LENGTH : maxLength;
    }
    
    private String deckedUpLine(int stringLength) {
        final String DECKED_CHAR = "-";
        String dottedlLine = "";
        for (int i = 0; i < stringLength; i++) {
            dottedlLine = dottedlLine + DECKED_CHAR;
        }
        
        return dottedlLine;
    }
}
