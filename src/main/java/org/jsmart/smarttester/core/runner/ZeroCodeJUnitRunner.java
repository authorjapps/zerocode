package org.jsmart.smarttester.core.runner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang.StringUtils;
import org.jsmart.smarttester.core.di.ApplicationMainModule;
import org.jsmart.smarttester.core.domain.ScenarioSpec;
import org.jsmart.smarttester.core.domain.SmartTestCase;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.engine.assertion.AssertionReport;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZeroCodeJUnitRunner extends BlockJUnit4ClassRunner {
    private static final Logger logger = LoggerFactory.getLogger(ZeroCodeJUnitRunner.class);

    static int i = 1;
    protected boolean passed;
    protected boolean testRunCompleted;
    private MultiStepsScenarioRunner multiStepsScenarioRunner;
    private final Class<?> testClass;
    Injector injector;
    SmartUtils smartUtils;

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    List<String> smartTestCaseNames = new ArrayList<>();
    String currentTestCase;

    public ZeroCodeJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);

        this.testClass = klass;
        this.smartUtils = getInjectedSmartUtilsClass();

        smartTestCaseNames = getSmartChildrenList();
    }

    private List<String> getSmartChildrenList() {
        List<FrameworkMethod> children = getChildren();
        children.forEach(
                frameworkMethod -> {
                    SmartTestCase annotation = frameworkMethod.getAnnotation(SmartTestCase.class);
                    if (annotation != null) {
                        smartTestCaseNames.add(annotation.value());
                    } else {
                        smartTestCaseNames.add(frameworkMethod.getName());
                    }
                }
        );

        return smartTestCaseNames;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        SmartTestCase annotation = method.getMethod().getAnnotation(SmartTestCase.class);

        if (annotation != null) {
            currentTestCase = annotation.value();
        } else {
            currentTestCase = method.getName();
        }

        /**
         * Capability tests, Navigation SUCCESS
         */
        final Description description = describeChild(method);

        notifier.fireTestStarted(description);

        logger.debug("### Running currentTestCase : " + currentTestCase);

        ScenarioSpec child = null;
        try {
            child = smartUtils.jsonFileToJava(currentTestCase, ScenarioSpec.class);
            logger.debug("### Found currentTestCase : -" + child);

            passed = getInjectedMultiStepsRunner().runScenario(child, new ScenarioSingleStepStatusNotifier() {

                @Override
                public Boolean notifyFlowStepAssertionFailed(String scenarioName,
                                                             String stepName,
                                                             List<AssertionReport> failureReportList) {

                    // Generate error report and display in the console stating which expectation(s) did not match
                    notifier.fireTestFailure(new Failure(description, new RuntimeException(
                            String.format("Assertion failed for step %s, details:%n%s%n", stepName, StringUtils.join(failureReportList, "\n"))
                    )));

                    return false;
                }

                @Override
                public Boolean notifyFlowStepExecutionException(String scenarioName,
                                                                String stepName,
                                                                Exception stepException) {

                    logger.info(String.format("Exception occurred while executing Scenario: %s, Step: %s, Details: %s",
                            scenarioName, stepName, stepException));
                    notifier.fireTestFailure(new Failure(description, stepException));

                    return false;
                }

                @Override
                public Boolean notifyFlowStepExecutionPassed(String scenarioName, String stepName) {
                    logger.info(String.format("\n**Step PASSED:%s --> %s\n", scenarioName, stepName));
                    return true;
                }

            });
        } catch (Exception ioEx) {
            ioEx.printStackTrace();
            notifier.fireTestFailure(new Failure(description, ioEx));
        }

        testRunCompleted = true;

        if (passed) {
            logger.info(String.format("\n**FINISHED executing all Steps for [%s] **.\nSteps were:%s",
                    child.getScenarioName(),
                    child.getSteps().stream().map(step -> step.getName()).collect(Collectors.toList())));
            notifier.fireTestFinished(description);
        }
    }

    public List<String> getSmartTestCaseNames() {
        return smartTestCaseNames;
    }

    public String getCurrentTestCase() {
        return currentTestCase;
    }

    private MultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        multiStepsScenarioRunner = getInjector().getInstance(MultiStepsScenarioRunner.class);
        return multiStepsScenarioRunner;
    }

    public Injector getInjector() {
        //TODO: Synchronise this if needed with e.g. synchronized (IptSmartRunner.class) {}
        final TargetEnv envAnnotation = testClass.getAnnotation(TargetEnv.class);
        String serverEnv = envAnnotation != null ? envAnnotation.value() : "config_hosts.properties";
        injector = Guice.createInjector(new ApplicationMainModule(serverEnv));
        return injector;
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getInjector().getInstance(SmartUtils.class);
    }
}
