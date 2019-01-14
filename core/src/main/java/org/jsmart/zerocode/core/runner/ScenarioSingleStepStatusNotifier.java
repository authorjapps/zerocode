package org.jsmart.zerocode.core.runner;

import org.jsmart.zerocode.core.engine.assertion.AssertionReport;

import java.util.List;

public interface ScenarioSingleStepStatusNotifier {
    Boolean notifyFlowStepAssertionFailed(String scenarioName,
                                          String stepName,
                                          List<AssertionReport> failureReportList);
    Boolean notifyFlowStepExecutionException(String scenarioName, String stepName, Exception ex);
    Boolean notifyFlowStepExecutionPassed(String scenarioName, String stepName);
}
