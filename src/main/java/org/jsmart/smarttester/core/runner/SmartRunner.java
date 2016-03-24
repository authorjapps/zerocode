package org.jsmart.smarttester.core.runner;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.jsmart.smarttester.core.di.SmartServiceModule;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

public class SmartRunner extends ParentRunner<FlowSpec> {

    private MultiStepsRunner multiStepsRunner;
    private final Class<?> testClass;
    List<FlowSpec> flowSpecs;
    Injector injector;
    SmartUtils smartUtils;

    protected Description flowDescription;
    protected boolean isRunSuccess;
    protected boolean passed;
    protected boolean isComplete;

    public SmartRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;

        /**
         * Get the SmartUtil, MultiStepsRunner injected from the main guice-Module.
         */
        this.multiStepsRunner = getInjectedMultiStepsRunner();
        this.smartUtils = getInjectedSmartUtilsClass();
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getInjector().getInstance(SmartUtils.class);
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
        TestPackageRoot rootPackageAnnotation = testClass.getAnnotation(TestPackageRoot.class);
        if(rootPackageAnnotation == null){
            throw new RuntimeException("Ah! Almost there. Just missing root package details." +
                    "\ne.g. Annotate your Test class now, e.g. @TestPackageRoot(\"resource_folder_for_test_cases\")");
        }

        return smartUtils.getFlowSpecListByPackage(rootPackageAnnotation.value());
    }

    /**
     * Returns a {@link Description} for {@code child}, which can be assumed to
     * be an element of the list returned by {@link ParentRunner#getChildren()}
     *
     * @param child
     */
    @Override
    protected Description describeChild(FlowSpec child) {

        this.flowDescription = Description.createTestDescription(testClass, child.getFlowName());
        return flowDescription;

        /**
         * Commented for right click, fix and enable, Try for IntelliJ or Eclipse
         */
        //Class<?> clazz, String name, Annotation... annotations
        /*Annotation annotation = null;
        try {
            annotation = (Annotation)Class.forName("org.junit.Test").newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Right Click Error. Ah, error while introducing annotation" + e);
        }

        //Description testDescription = Description.createTestDescription(testClass, child.getFlowName(), annotation);
        */
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

        // Notify that this single test was started.
        // Supply the flowDescription
        notifier.fireTestStarted(flowDescription);

        /*passed = getInjectedMultiStepsRunner().runSteps(child, new FlowRunningObserver() {
            @Override
            public void testRanSuccessFully() {
                isRunSuccess = true;
            }
        });*/

        //
        passed = getInjectedMultiStepsRunner().runChildStep(child,
                (flowName, stepName) -> {
                    isRunSuccess = true;
                }
        );
        //
        if (passed) {
            notifier.fireTestFinished(flowDescription);
        }
    }

    private MultiStepsRunner getInjectedMultiStepsRunner() {
        multiStepsRunner = getInjector().getInstance(MultiStepsRunner.class);
        return multiStepsRunner;
    }

    public Injector getInjector() {
        //Synchronise this with e.g. synchronized (IptSmartRunner.class) {}
        injector = Guice.createInjector(new SmartServiceModule());
        return injector;
    }

    public void setSmartUtils(SmartUtils smartUtils) {
        this.smartUtils = smartUtils;
    }

    public boolean isRunSuccess() {
        return isRunSuccess;
    }

    public boolean isPassed() {
        return passed;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setMultiStepsRunner(MultiStepsRunner multiStepsRunner) {
        this.multiStepsRunner = multiStepsRunner;
    }
}
