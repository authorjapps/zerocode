package org.jsmart.zerocode.core.runner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestListener;
import org.jsmart.zerocode.core.utils.SmartUtils;
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

public class ZeroCodeUnitRunner extends BlockJUnit4ClassRunner {
    private static final Logger logger = LoggerFactory.getLogger(ZeroCodeUnitRunner.class);

    static int i = 1;
    protected boolean passed;
    protected boolean testRunCompleted;
    private ZeroCodeMultiStepsScenarioRunner zeroCodeMultiStepsScenarioRunner;
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

    public ZeroCodeUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);

        this.testClass = klass;
        this.smartUtils = getInjectedSmartUtilsClass();

        smartTestCaseNames = getSmartChildrenList();
    }

    private List<String> getSmartChildrenList() {
        List<FrameworkMethod> children = getChildren();
        children.forEach(
                frameworkMethod -> {
                    JsonTestCase annotation = frameworkMethod.getAnnotation(JsonTestCase.class);
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
    public void run(RunNotifier notifier){
        notifier.addListener(new ZeroCodeTestListener(smartUtils.getMapper()));
        super.run(notifier);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        JsonTestCase annotation = method.getMethod().getAnnotation(JsonTestCase.class);

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

            passed = getInjectedMultiStepsRunner().runScenario(child, notifier, description);

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

    private ZeroCodeMultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        zeroCodeMultiStepsScenarioRunner = getMainModuleInjector().getInstance(ZeroCodeMultiStepsScenarioRunner.class);
        return zeroCodeMultiStepsScenarioRunner;
    }

    public Injector getMainModuleInjector() {
        // TODO: Synchronise this if needed with e.g. synchronized (IptSmartRunner.class) {}
        final TargetEnv envAnnotation = testClass.getAnnotation(TargetEnv.class);
        String serverEnv = envAnnotation != null ? envAnnotation.value() : "config_hosts.properties";
        injector = Guice.createInjector(new ApplicationMainModule(serverEnv));
        return injector;
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getMainModuleInjector().getInstance(SmartUtils.class);
    }
}
