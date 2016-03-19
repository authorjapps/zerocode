package org.jsmart.smarttester.core.runner;

public interface MultiStepsRunner<T, Q> {

    boolean runSteps(T t, Q q);
}
