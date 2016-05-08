package org.jsmart.smarttester.core.runner;

import java.util.List;

public interface FlowStepStatusNotifier {
    public Boolean notifyFlowStepAssertionFailed(String flowName,
                                              String stepName,
                                              List<JsonAssertionFailureResult> failureReportList);
    public Boolean notifyFlowStepExecutionException(String flowName, String stepName, Exception ex);
    public Boolean notifyFlowStepExecutionPassed(String flowName, String stepName);
}
