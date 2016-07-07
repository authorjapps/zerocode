package org.jsmart.smarttester.core.runner;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.jsmart.smarttester.core.di.ApplicationMainModule;
import org.jsmart.smarttester.core.domain.ScenarioSpec;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.domain.TestPackageRoot;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZeroCodePackageRunner extends ParentRunner<ScenarioSpec> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ZeroCodePackageRunner.class);

    private MultiStepsScenarioRunner multiStepsScenarioRunner;
    private final Class<?> testClass;
    List<ScenarioSpec> scenarioSpecs;
    Injector injector;
    SmartUtils smartUtils;

    protected Description flowDescription;
    protected boolean isRunSuccess;
    protected boolean passed;
    protected boolean testRunCompleted;

    public ZeroCodePackageRunner(Class<?> testClass) throws InitializationError {
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
    public ZeroCodePackageRunner(Class<?> testClass, SmartUtils smartUtils) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
        this.smartUtils = smartUtils;
    }

    /**
     * Returns a list of objects that define the children of this Runner.
     */
    @Override
    protected List<ScenarioSpec> getChildren() {
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
    protected Description describeChild(ScenarioSpec child) {

        this.flowDescription = Description.createTestDescription(testClass, child.getScenarioName());
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

        //Description testDescription = Description.createTestDescription(testClass, child.getScenarioName(), annotation);
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
    protected void runChild(ScenarioSpec child, RunNotifier notifier) {

        final Description description = Description.createTestDescription(testClass, child.getScenarioName());

        // Notify that this single test has been started.
        // Supply the scenario/journey name
        notifier.fireTestStarted(description);

        passed = getInjectedMultiStepsRunner().runScenario(child, notifier, description);

        testRunCompleted = true;

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
