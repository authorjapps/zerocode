package org.jsmart.smarttester.core.runner;

import org.jsmart.smarttester.core.domain.FlowSpec;

public interface MultiStepsRunner {

    boolean runSteps(FlowSpec flowSpec, FlowRunningObserver observer);
}
