package org.jsmart.smarttester.core.runner;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

public class SmartRunner extends ParentRunner<FlowSpec> {

    @Inject
    SmartUtils smartUtils;

    private MultiStepsRunner<FlowSpec, FlowRunningListener> multiStepsRunner;
    private final Class<?> testClass;
    List<FlowSpec> flowSpecs;

    @Inject
    public SmartRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;

    }

    @Inject
    public SmartRunner(Class<?> testClass, SmartUtils smartUtils) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.smartUtils = smartUtils;
    }
    /**
     * Returns a list of objects that define the children of this Runner.
     */
    @Override
    protected List<FlowSpec> getChildren() {
        String packageName = testClass.getAnnotation(TestPackageRoot.class).value();

        return smartUtils.getFlowSpecListByPackage(packageName);
    }

    /**
     * Returns a {@link Description} for {@code child}, which can be assumed to
     * be an element of the list returned by {@link ParentRunner#getChildren()}
     *
     * @param child
     */
    @Override
    protected Description describeChild(FlowSpec child) {
        return null;
    }

    /**
     * Runs the test corresponding to {@code child}, which can be assumed to be
     * an element of the list returned by {@link ParentRunner#getChildren()}.
     * Subclasses are responsible for making sure that relevant test events are
     * reported through {@code notifier}
     *
     * @param child
     * @param notifier
     */
    @Override
    protected void runChild(FlowSpec child, RunNotifier notifier) {

    }


}
