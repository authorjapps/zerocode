package org.jsmart.zerocode.core.runner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.di.RuntimeHttpClientModule;
import org.jsmart.zerocode.core.domain.HostProperties;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestReportListener;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.RestEasyDefaultHttpClient;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
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

import static org.jsmart.zerocode.core.utils.RunnerUtils.getEnvSpecificConfigFile;

public class ZeroCodeUnitRunner extends BlockJUnit4ClassRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeUnitRunner.class);

    static int i = 1;
    protected boolean passed;
    protected boolean testRunCompleted;
    private ZeroCodeMultiStepsScenarioRunner zeroCodeMultiStepsScenarioRunner;
    private final Class<?> testClass;
    Injector injector;
    SmartUtils smartUtils;

    private HostProperties hostProperties;
    private String host;
    private String context;
    private int port;
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

        /*
         * Read the host, port, context etc from the inline annotation instead of a properties file
         */
        hostProperties = testClass.getAnnotation(HostProperties.class);

        if (hostProperties != null) {
            host = hostProperties.host();
            port = hostProperties.port();
            context = hostProperties.context();
        }
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
        notifier.addListener(new ZeroCodeTestReportListener(smartUtils.getMapper(), getInjectedReportGenerator()));
        super.run(notifier);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        final Description description = describeChild(method);

        if (isIgnored(method)) {
            notifier.fireTestIgnored(description);
        } else {
            JsonTestCase annotation = method.getMethod().getAnnotation(JsonTestCase.class);

            if (annotation != null) {
                currentTestCase = annotation.value();
            } else {
                currentTestCase = method.getName();
            }

            notifier.fireTestStarted(description);

            LOGGER.debug("### Running currentTestCase : " + currentTestCase);

            ScenarioSpec child = null;
            try {
                child = smartUtils.jsonFileToJava(currentTestCase, ScenarioSpec.class);

                LOGGER.debug("### Found currentTestCase : -" + child);

                final ZeroCodeMultiStepsScenarioRunner multiStepsRunner = getInjectedMultiStepsRunner();

                /*
                 * Override the properties file containing hosts and ports with HostProperties
                 * only if the annotation is present on the runner.
                 */
                if (hostProperties != null) {
                    ((ZeroCodeMultiStepsScenarioRunnerImpl) multiStepsRunner).overrideHost(host);
                    ((ZeroCodeMultiStepsScenarioRunnerImpl) multiStepsRunner).overridePort(port);
                    ((ZeroCodeMultiStepsScenarioRunnerImpl) multiStepsRunner).overrideApplicationContext(context);
                }
                passed = multiStepsRunner.runScenario(child, notifier, description);

            } catch (Exception ioEx) {
                ioEx.printStackTrace();
                notifier.fireTestFailure(new Failure(description, ioEx));
            }

            testRunCompleted = true;

            if (passed) {
                LOGGER.info(String.format("\n**FINISHED executing all Steps for [%s] **.\nSteps were:%s",
                        child.getScenarioName(),
                        child.getSteps().stream().map(step -> step.getName()).collect(Collectors.toList())));
            }

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
        // Synchronise this with an object lock e.g. synchronized (ZeroCodeUnitRunner.class) {}
        synchronized (this) {
            final TargetEnv envAnnotation = testClass.getAnnotation(TargetEnv.class);
            String serverEnv = envAnnotation != null ? envAnnotation.value() : "config_hosts.properties";

            serverEnv = getEnvSpecificConfigFile(serverEnv, testClass);

            final UseHttpClient httpClientAnnotated = testClass.getAnnotation(UseHttpClient.class);
            Class<? extends BasicHttpClient> runtimeHttpClient =
                    httpClientAnnotated != null ? httpClientAnnotated.value() : RestEasyDefaultHttpClient.class;

            injector = Guice.createInjector(Modules.override(new ApplicationMainModule(serverEnv))
                    .with(new RuntimeHttpClientModule(runtimeHttpClient)));

            return injector;
        }
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getMainModuleInjector().getInstance(SmartUtils.class);
    }

    protected ZeroCodeReportGenerator getInjectedReportGenerator() {
        return getMainModuleInjector().getInstance(ZeroCodeReportGenerator.class);
    }

}
