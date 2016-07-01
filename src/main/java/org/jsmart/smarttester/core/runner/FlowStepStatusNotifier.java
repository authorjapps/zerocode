package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.engine.assertion.AssertionReport;

import java.util.List;

public interface FlowStepStatusNotifier {
    public Boolean notifyFlowStepAssertionFailed(String scenarioName,
                                              String stepName,
                                              List<AssertionReport> failureReportList);
    public Boolean notifyFlowStepExecutionException(String scenarioName, String stepName, Exception ex);
    public Boolean notifyFlowStepExecutionPassed(String scenarioName, String stepName);
}
