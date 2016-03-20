package org.jsmart.smarttester.core.runner;

public class DefaultSmartMultiStepsRunner<T, Q>  implements MultiStepsRunner<T, Q> {
    @Override
    public boolean runSteps(T t, Q q) {
        return true;
    }
}
