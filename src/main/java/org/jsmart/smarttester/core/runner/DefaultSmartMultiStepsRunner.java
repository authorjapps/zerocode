package org.jsmart.smarttester.core.runner;

import com.google.inject.Singleton;
import org.jsmart.smarttester.core.domain.FlowSpec;

@Singleton
public class DefaultSmartMultiStepsRunner  implements MultiStepsRunner{
    @Override
    public boolean runSteps(FlowSpec flowSpec, FlowRunningObserver flowRunningObserver) {

        flowRunningObserver.testRanSuccessFully();

        return true;
    }
}
