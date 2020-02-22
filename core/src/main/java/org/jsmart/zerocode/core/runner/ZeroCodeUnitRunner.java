package org.jsmart.zerocode.core.runner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.module.RuntimeHttpClientModule;
import org.jsmart.zerocode.core.di.module.RuntimeKafkaClientModule;
import org.jsmart.zerocode.core.domain.*;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeIoWriteBuilder;
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestReportListener;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.jsmart.zerocode.core.logbuilder.ZerocodeCorrelationshipLogger;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.CHARTS_AND_CSV;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.ZEROCODE_JUNIT;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecReportBuilder.newInstance;
import static org.jsmart.zerocode.core.utils.RunnerUtils.*;

public class ZeroCodeUnitRunner extends BlockJUnit4ClassRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeUnitRunner.class);

    private ZeroCodeMultiStepsScenarioRunner zeroCodeMultiStepsScenarioRunner;
    private final Class<?> testClass;
    private Injector injector;
    private SmartUtils smartUtils;
    private HostProperties hostProperties;
    private String host;
    private String context;
    private int port;
    private List<String> smartTestCaseNames = new ArrayList<>();
    private String currentTestCase;
    private ZerocodeCorrelationshipLogger corrLogger;
    protected boolean testRunCompleted;
    protected boolean passed;

    private ZeroCodeMultiStepsScenarioRunner multiStepsRunner;

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public ZeroCodeUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);

        this.testClass = klass;
        this.smartUtils = getInjectedSmartUtilsClass();

        this.smartTestCaseNames = getSmartChildrenList();

        /*
         * Read the host, port, context etc from the inline annotation instead of a properties file
         */
        this.hostProperties = testClass.getAnnotation(HostProperties.class);

        if (this.hostProperties != null) {
            this.host = hostProperties.host();
            this.port = hostProperties.port();
            this.context = hostProperties.context();
        }

        this.multiStepsRunner = createZeroCodeMultiStepRunner();
    }

    @Override
    public void run(RunNotifier notifier) {
        ZeroCodeTestReportListener reportListener = new ZeroCodeTestReportListener(smartUtils.getMapper(), getInjectedReportGenerator());

        LOGGER.info("System property " + ZEROCODE_JUNIT + "=" + getProperty(ZEROCODE_JUNIT));
        if (!CHARTS_AND_CSV.equals(getProperty(ZEROCODE_JUNIT))) {
            notifier.addListener(reportListener);
        }

        super.run(notifier);

        handleNoRunListenerReport(reportListener);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {

        final Description description = describeChild(method);
        JsonTestCase jsonTestCaseAnno = method.getMethod().getAnnotation(JsonTestCase.class);
        if (jsonTestCaseAnno == null) {
            jsonTestCaseAnno = evalScenarioToJsonTestCase(method.getMethod().getAnnotation(Scenario.class));
        }

        if (isIgnored(method)) {

            notifier.fireTestIgnored(description);

        } else if (jsonTestCaseAnno != null) {
            runLeafJsonTest(method, notifier, description, jsonTestCaseAnno);

        } else {
            // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            // It is an usual Junit test, not the JSON test case
            // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
            runLeafJUnitTest(methodBlock(method), description, notifier);
        }

    }

    private Statement withRules(FrameworkMethod method, Object target,
                                Statement statement) {
        List<TestRule> testRules = getTestRules(target);
        Statement result = statement;
        result = withMethodRules(method, testRules, target, result);
        result = withTestRules(method, testRules, result);

        return result;
    }

    private Statement withTestRules(FrameworkMethod method, List<TestRule> testRules,
                                    Statement statement) {
        return testRules.isEmpty() ? statement :
                new RunRules(statement, testRules, describeChild(method));
    }

    private Statement withMethodRules(FrameworkMethod method, List<TestRule> testRules,
                                      Object target, Statement result) {
        for (org.junit.rules.MethodRule each : getMethodRules(target)) {
            if (!testRules.contains(each)) {
                result = each.apply(result, method, target);
            }
        }
        return result;
    }

    private List<org.junit.rules.MethodRule> getMethodRules(Object target) {
        return rules(target);
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

            Class<? extends BasicHttpClient> runtimeHttpClient = getCustomHttpClientOrDefault(testClass);
            Class<? extends BasicKafkaClient> runtimeKafkaClient = getCustomKafkaClientOrDefault(testClass);

            injector = Guice.createInjector(Modules.override(new ApplicationMainModule(serverEnv))
                    .with(
                            new RuntimeHttpClientModule(runtimeHttpClient),
                            new RuntimeKafkaClientModule(runtimeKafkaClient)
                    )
            );

            return injector;
        }
    }

    protected SmartUtils getInjectedSmartUtilsClass() {
        return getMainModuleInjector().getInstance(SmartUtils.class);
    }

    protected ZeroCodeReportGenerator getInjectedReportGenerator() {
        return getMainModuleInjector().getInstance(ZeroCodeReportGenerator.class);
    }

    private void runLeafJsonTest(FrameworkMethod method, RunNotifier notifier, Description description, JsonTestCase jsonTestCaseAnno) {
        Object test;
        try {
            test = new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest();
                }
            }.run();
        } catch (Throwable e) {
            return;
        }

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (jsonTestCaseAnno != null) {
                    currentTestCase = jsonTestCaseAnno.value();
                }
                notifier.fireTestStarted(description);

                LOGGER.debug("### Running currentTestCase : " + currentTestCase);
                ScenarioSpec child = null;
                try {
                    child = smartUtils.scenarioFileToJava(currentTestCase, ScenarioSpec.class);

                    LOGGER.debug("### Found currentTestCase : -" + child);

                    passed = multiStepsRunner.runScenario(child, notifier, description);
                } catch (Throwable ioEx) {
                    ioEx.printStackTrace();
                    notifier.fireTestFailure(new Failure(description, ioEx));
                    throw ioEx;
                } finally {
                    testRunCompleted = true;

                    if (passed) {
                        LOGGER.info(String.format("\n**FINISHED executing all Steps for [%s] **.\nSteps were:%s",
                                child.getScenarioName(),
                                child.getSteps().stream()
                                        .map(step -> step.getName() == null ? step.getId() : step.getName())
                                        .collect(Collectors.toList())));
                    }

                    notifier.fireTestFinished(description);
                    throw new RuntimeException();
                }
            }
        };
        statement = possiblyExpectingExceptions(method, test, statement);
        statement = withPotentialTimeout(method, test, statement);
        statement = withBefores(method, test, statement);
        statement = withAfters(method, test, statement);
        statement = withRules(method, test, statement);
        try {
            statement.evaluate();
        } catch (Throwable e) {
        }

    }

    private List<String> getSmartChildrenList() {
        List<FrameworkMethod> children = getChildren();
        children.forEach(
                frameworkMethod -> {
                    JsonTestCase jsonTestCaseAnno = frameworkMethod.getAnnotation(JsonTestCase.class);

                    if (jsonTestCaseAnno == null) {
                        jsonTestCaseAnno = evalScenarioToJsonTestCase(frameworkMethod.getAnnotation(Scenario.class));
                    }

                    if (jsonTestCaseAnno != null) {
                        smartTestCaseNames.add(jsonTestCaseAnno.value());
                    } else {
                        smartTestCaseNames.add(frameworkMethod.getName());
                    }
                }
        );

        return smartTestCaseNames;
    }

    private ZeroCodeMultiStepsScenarioRunner createZeroCodeMultiStepRunner() {
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
        return multiStepsRunner;
    }

    private final void runLeafJUnitTest(Statement statement, Description description,
                                        RunNotifier notifier) {
        LOGGER.info("Running a pure JUnit test...");

        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();

        final String logPrefixRelationshipId = prepareRequestReport(description);

        try {
            statement.evaluate();
            passed = true;
            LOGGER.info("JUnit test passed = {} ", passed);

        } catch (AssumptionViolatedException e) {
            passed = false;
            LOGGER.warn("JUnit test failed due to : {},  passed = {}", e, passed);

            eachNotifier.addFailedAssumption(e);

        } catch (Throwable e) {
            passed = false;
            LOGGER.warn("JUnit test failed due to : {},  passed = {}", e, passed);

            eachNotifier.addFailure(e);

        } finally {
            LOGGER.info("JUnit test run completed. See the results in the console or log.  passed = {}", passed);
            prepareResponseReport(logPrefixRelationshipId);
            buildReportAndPrintToFile(description);

            eachNotifier.fireTestFinished();
        }
    }


    private void buildReportAndPrintToFile(Description description) {
        ZeroCodeExecReportBuilder reportResultBuilder = newInstance().loop(0).scenarioName(description.getClassName());
        reportResultBuilder.step(corrLogger.buildReportSingleStep());

        ZeroCodeIoWriteBuilder reportBuilder = ZeroCodeIoWriteBuilder.newInstance().timeStamp(LocalDateTime.now());
        reportBuilder.result(reportResultBuilder.build());
        reportBuilder.printToFile(description.getClassName() + corrLogger.getCorrelationId() + ".json");
    }

    private void prepareResponseReport(String logPrefixRelationshipId) {
        LocalDateTime timeNow = LocalDateTime.now();
        LOGGER.info("JUnit *responseTimeStamp:{}, \nJUnit Response:{}", timeNow, logPrefixRelationshipId);
        corrLogger.aResponseBuilder()
                .relationshipId(logPrefixRelationshipId)
                .responseTimeStamp(timeNow);

        corrLogger.stepOutcome(passed);
        corrLogger.buildResponseDelay();
    }

    private String prepareRequestReport(Description description) {
        corrLogger = ZerocodeCorrelationshipLogger.newInstance(LOGGER);
        corrLogger.stepLoop(0);
        final String logPrefixRelationshipId = corrLogger.createRelationshipId();
        LocalDateTime timeNow = LocalDateTime.now();
        corrLogger.aRequestBuilder()
                .stepLoop(0)
                .relationshipId(logPrefixRelationshipId)
                .requestTimeStamp(timeNow)
                .step(description.getMethodName());
        LOGGER.info("JUnit *requestTimeStamp:{}, \nJUnit Request:{}", timeNow, logPrefixRelationshipId);
        return logPrefixRelationshipId;
    }

    private void handleNoRunListenerReport(ZeroCodeTestReportListener reportListener) {
        if (CHARTS_AND_CSV.equals(getProperty(ZEROCODE_JUNIT))) {
            /**
             * Gradle does not support JUnit RunListener. Hence Zerocode gracefully handled this
             * upon request from Gradle users. But this is not limited to Gradle, anywhere you
             * want to bypass the JUnit RunListener, you can achieve this way.
             * See README for details.
             *
             * There are number of tickets opened for this, but not yet fixed.
             * - https://discuss.gradle.org/t/testrunfinished-not-run-in-junit-integration/14644
             * - https://github.com/gradle/gradle/issues/842
             * - many more related tickets.
             */
            LOGGER.debug("Bypassed JUnit RunListener [as configured by the build tool] to generate useful reports...");
            reportListener.testRunFinished(new Result());
        }
    }

    private JsonTestCase evalScenarioToJsonTestCase(Scenario scenario) {
        // ---------------------------------------------------
        // If Scenario is present then convert to JsonTestCase
        // ---------------------------------------------------
        JsonTestCase jsonTestCase = new JsonTestCase() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonTestCase.class;
            }

            @Override
            public String value() {
                return scenario != null ? scenario.value() : null;
            }
        };

        return jsonTestCase.value() == null ? null : jsonTestCase;
    }

}
