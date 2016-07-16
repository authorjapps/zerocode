package org.jsmart.smarttester.core.runner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.catalina.LifecycleException;
import org.jsmart.smarttester.core.di.ApplicationMainModule;
import org.jsmart.smarttester.core.domain.JsonTestCase;
import org.jsmart.smarttester.core.domain.ScenarioSpec;
import org.jsmart.smarttester.core.domain.TargetEnv;
import org.jsmart.smarttester.core.listener.EmbeddedTomcatJunitListener;
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

public class ZeroCodeTomcatUnitRunner extends BlockJUnit4ClassRunner {
    private static final Logger logger = LoggerFactory.getLogger(ZeroCodeTomcatUnitRunner.class);

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
    private boolean runningViaSuite;

    public ZeroCodeTomcatUnitRunner(Class<?> klass) throws InitializationError {
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
    public void run(RunNotifier notifier) {
        //
        logger.info("### Starting tomcat at run()...............");
        //notifier.addListener(new EmbeddedTomcatJunitListener(43210, "target/war", ""));

        EmbeddedTomcatJunitListener tomcatwithWebApp = new EmbeddedTomcatJunitListener(43210, "target/wars", "/ipt-ss-address-management-services");
        if (!runningViaSuite) {
            try {
                tomcatwithWebApp.startNow();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.run(notifier);

        if (!runningViaSuite) {
            try {
                tomcatwithWebApp.stopNow();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }
        }

        logger.info("### run() finished. Tomcat stopped...............");

        testRunCompleted = true;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        //
        logger.info("### running Child ...............");
        //

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

        //
        logger.info("### child finished running...............");
        //

    }

    public List<String> getSmartTestCaseNames() {
        return smartTestCaseNames;
    }

    public String getCurrentTestCase() {
        return currentTestCase;
    }

    private MultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        multiStepsScenarioRunner = getMainModuleInjector().getInstance(MultiStepsScenarioRunner.class);
        return multiStepsScenarioRunner;
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

    public void setRunningViaSuite(boolean runningViaSuite) {
        this.runningViaSuite = runningViaSuite;
    }
}
