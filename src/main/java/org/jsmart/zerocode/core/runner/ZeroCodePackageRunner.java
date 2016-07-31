package org.jsmart.zerocode.core.runner;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.di.RuntimeHttpClientModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestListener;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.RestEasyDefaultHttpClient;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.inject.Guice.createInjector;

public class ZeroCodePackageRunner extends ParentRunner<ScenarioSpec> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ZeroCodePackageRunner.class);

    private ZeroCodeMultiStepsScenarioRunner zeroCodeMultiStepsScenarioRunner;
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
         * Get the SmartUtil, ZeroCodeMultiStepsScenarioRunner injected from the main guice-Module.
         */
        this.zeroCodeMultiStepsScenarioRunner = getInjectedMultiStepsRunner();
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

        /*
         * Different scenarios with same name -or- Same scenarios with same name more than once is prevented
         */
        smartUtils.checkDuplicateScenarios(rootPackageAnnotation.value());

        return smartUtils.getScenarioSpecListByPackage(rootPackageAnnotation.value());
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

    @Override
    public void run(RunNotifier notifier){
        notifier.addListener(new ZeroCodeTestListener(smartUtils.getMapper(), getInjectedReportGenerator()));
        super.run(notifier);
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

    private ZeroCodeMultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        zeroCodeMultiStepsScenarioRunner = getInjector().getInstance(ZeroCodeMultiStepsScenarioRunner.class);
        return zeroCodeMultiStepsScenarioRunner;
    }

    private ZeroCodeReportGenerator getInjectedReportGenerator() {
        return getInjector().getInstance(ZeroCodeReportGenerator.class);
    }

    public Injector getInjector() {
        //TODO: Synchronise this with e.g. synchronized (ZeroCodePackageRunner.class) {}
        final TargetEnv envAnnotation = testClass.getAnnotation(TargetEnv.class);
        String serverEnv = envAnnotation != null? envAnnotation.value() : "config_hosts.properties";

        final UseHttpClient runtimeClientAnnotated = testClass.getAnnotation(UseHttpClient.class);
        Class<? extends BasicHttpClient> runtimeHttpClient = runtimeClientAnnotated != null ? runtimeClientAnnotated.value() : RestEasyDefaultHttpClient.class;

        return createInjector(Modules.override(new ApplicationMainModule(serverEnv)).with(new RuntimeHttpClientModule(runtimeHttpClient)));
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

    public void setZeroCodeMultiStepsScenarioRunner(ZeroCodeMultiStepsScenarioRunner zeroCodeMultiStepsScenarioRunner) {
        this.zeroCodeMultiStepsScenarioRunner = zeroCodeMultiStepsScenarioRunner;
    }
}
