package org.jsmart.smarttester.core.runner;

import com.google.inject.Inject;
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

    protected Description flowDescription;
    protected boolean isRunSuccess;
    protected boolean passed;
    protected boolean isComplete;

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
        TestPackageRoot rootPackageAnnotation = testClass.getAnnotation(TestPackageRoot.class);
        if(rootPackageAnnotation == null){
            throw new RuntimeException("Ah! Almost there. Missing root package details." +
                    "e.g. Annotate your Test class e.g. @TestPackageRoot(\"resource_folder_for_test_cases\")\n");
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
        notifier.fireTestStarted(getDescription());

        passed = multiStepsRunner.runSteps(child, new FlowRunningListener() {
            @Override
            public void testRanSuccessFully() {
                isRunSuccess = true;
            }
        });

        if (passed) {
            notifier.fireTestFinished(flowDescription);
        }
    }

    private MultiStepsRunner<Object, Object> getMultiStepsRunner() {
        return null;
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

    public void setMultiStepsRunner(MultiStepsRunner<FlowSpec, FlowRunningListener> multiStepsRunner) {
        this.multiStepsRunner = multiStepsRunner;
    }
}
