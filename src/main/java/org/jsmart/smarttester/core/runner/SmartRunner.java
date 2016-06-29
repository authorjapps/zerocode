package org.jsmart.smarttester.core.runner;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.commons.lang.StringUtils;
import org.jsmart.smarttester.core.di.ApplicationMainModule;
import org.jsmart.smarttester.core.domain.FlowSpec;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.domain.TestPackageRoot;
import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SmartRunner extends ParentRunner<FlowSpec> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SmartRunner.class);

    private MultiStepsScenarioRunner multiStepsScenarioRunner;
    private final Class<?> testClass;
    List<FlowSpec> flowSpecs;
    Injector injector;
    SmartUtils smartUtils;

    protected Description flowDescription;
    protected boolean isRunSuccess;
    protected boolean passed;
    protected boolean testRunCompleted;

    public SmartRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;

        /**
         * Get the SmartUtil, MultiStepsScenarioRunner injected from the main guice-Module.
         */
        this.multiStepsScenarioRunner = getInjectedMultiStepsRunner();
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
        if (rootPackageAnnotation == null) {
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

        /*
         * Commented for right click, fix and enable, Try with IntelliJ or Eclipse, see if it works
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

        final Description description = Description.createTestDescription(testClass, child.getFlowName());

        // Notify that this single test has been started.
        // Supply the scenario/journey name
        notifier.fireTestStarted(description);

        passed = getInjectedMultiStepsRunner().runSteps(child, new FlowStepStatusNotifier() {

            @Override
            public Boolean notifyFlowStepAssertionFailed(String flowName,
                                                      String stepName,
                                                      List<AssertionReport> failureReportList) {

                // Generate error report and display in the console stating which expectation(s) did not match
                notifier.fireTestFailure(new Failure(description, new RuntimeException(
                        String.format( "Assertion failed for step %s, details:%n%s%n", stepName, StringUtils.join(failureReportList, "\n"))
                )));

                return false;
            }

            @Override
            public Boolean notifyFlowStepExecutionException(String flowName,
                                                         String stepName,
                                                         Exception stepException) {

                logger.info(String.format("Exception occurred while executing Scenario: %s, Step: %s, Details: %s",
                        flowName, stepName, stepException));
                notifier.fireTestFailure(new Failure(description, stepException));

                return false;
            }

            @Override
            public Boolean notifyFlowStepExecutionPassed(String flowName, String stepName) {
                // log that flowname with stepname passed.
                logger.info(String.format("\n***Step PASSED:%s->%s", flowName, stepName));
                return true;
            }

        });

        testRunCompleted = true;

        //
        if (passed) {
            notifier.fireTestFinished(description);
        }
    }

    private MultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        multiStepsScenarioRunner = getInjector().getInstance(MultiStepsScenarioRunner.class);
        return multiStepsScenarioRunner;
    }

    public Injector getInjector() {
        //TODO: Synchronise this with e.g. synchronized (IptSmartRunner.class) {}
        final TargetEnv envAnnotation = testClass.getAnnotation(TargetEnv.class);
        String serverEnv = envAnnotation != null? envAnnotation.value() : "config_hosts.properties";
        injector = Guice.createInjector(new ApplicationMainModule(serverEnv));
        //injector = Guice.createInjector(new ApplicationMainModule());
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

    public boolean isTestRunCompleted() {
        return testRunCompleted;
    }

    public void setMultiStepsScenarioRunner(MultiStepsScenarioRunner multiStepsScenarioRunner) {
        this.multiStepsScenarioRunner = multiStepsScenarioRunner;
    }
}
