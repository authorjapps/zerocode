package org.jsmart.zerocode.core.runner;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.module.RuntimeHttpClientModule;
import org.jsmart.zerocode.core.di.module.RuntimeKafkaClientModule;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.JsonTestCases;
import org.jsmart.zerocode.core.domain.Scenario;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.domain.Scenarios;
import org.jsmart.zerocode.core.domain.TargetEnv;
import org.jsmart.zerocode.core.domain.TestPackageRoot;
import org.jsmart.zerocode.core.domain.UseHttpClient;
import org.jsmart.zerocode.core.domain.UseKafkaClient;
import org.jsmart.zerocode.core.engine.listener.TestUtilityListener;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.jsmart.zerocode.core.kafka.client.ZerocodeCustomKafkaClient;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.inject.Guice.createInjector;
import static java.lang.System.getProperty;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.CHARTS_AND_CSV;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.ZEROCODE_JUNIT;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getEnvSpecificConfigFile;
import static org.jsmart.zerocode.core.utils.RunnerUtils.handleTestCompleted;

public class ZeroCodePackageRunner extends ParentRunner<ScenarioSpec> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodePackageRunner.class);

    private final Class<?> testClass;
    private List<ScenarioSpec> scenarioSpecs;
    private Injector injector;
    private SmartUtils smartUtils;
    protected Description scenarioDescription;
    protected boolean isRunSuccess;
    protected boolean passed;
    protected boolean testRunCompleted;
    private ZeroCodeMultiStepsScenarioRunner zeroCodeMultiStepsScenarioRunner;

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
        return getMainModuleInjector().getInstance(SmartUtils.class);
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
        JsonTestCases jsonTestCasesAnnotation = testClass.getAnnotation(JsonTestCases.class);
        Scenarios scenariosAnnotation = testClass.getAnnotation(Scenarios.class);
        validateSuiteAnnotationPresent(rootPackageAnnotation, jsonTestCasesAnnotation, scenariosAnnotation);

        if (rootPackageAnnotation != null) {
            /*
             * Different scenarios with same name -or- Same scenarios with same name more than once is prevented
             */
            smartUtils.checkDuplicateScenarios(rootPackageAnnotation.value());

            return smartUtils.getScenarioSpecListByPackage(rootPackageAnnotation.value());

        } else {
            List<String> allEndPointFiles = readTestScenarioFiles();

            return allEndPointFiles.stream()
                    .map(testResource -> {
                        try {
                            return smartUtils.scenarioFileToJava(testResource, ScenarioSpec.class);
                        } catch (IOException e) {
                            throw new RuntimeException("Exception while deserializing to Spec. Details: " + e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns a {@link Description} for {@code child}, which can be assumed to
     * be an element of the list returned by {@link ParentRunner#getChildren()}
     *
     */
    @Override
    protected Description describeChild(ScenarioSpec child) {

        this.scenarioDescription = Description.createTestDescription(testClass, child.getScenarioName());
        return scenarioDescription;

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
    public void run(RunNotifier notifier) {
        RunListener reportListener = createTestUtilityListener();
        notifier.addListener(reportListener);

        LOGGER.debug("System property " + ZEROCODE_JUNIT + "=" + getProperty(ZEROCODE_JUNIT));
        if (!CHARTS_AND_CSV.equals(getProperty(ZEROCODE_JUNIT))) {
            notifier.addListener(reportListener);
        }

        super.run(notifier);

        handleNoRunListenerReport(reportListener);
    }

    protected RunListener createTestUtilityListener() {
        return getMainModuleInjector().getInstance(TestUtilityListener.class);
    }


    /**
     * Runs the test corresponding to {@code child}, which can be assumed to be
     * an element of the list returned by {@link ParentRunner#getChildren()}.
     * Subclasses are responsible for making sure that relevant test events are
     * reported through {@code notifier}
     *
     */
    @Override
    protected void runChild(ScenarioSpec child, RunNotifier notifier) {

        final Description description = Description.createTestDescription(testClass, child.getScenarioName());

        // ----------------------------------------------
        // Notify that this single test has been started.
        // Supply the scenario/journey name
        // ----------------------------------------------
        notifier.fireTestStarted(description);

        passed = zeroCodeMultiStepsScenarioRunner.runScenario(child, notifier, description);

        testRunCompleted = true;

        if (passed) {
            LOGGER.debug(String.format("\nPackageRunner- **FINISHED executing all Steps for [%s] **.\nSteps were:%s",
                    child.getScenarioName(),
                    child.getSteps().stream().map(step -> step.getName()).collect(Collectors.toList())));
        }

        notifier.fireTestFinished(description);

    }

    // This is exact duplicate of ZeroCodeUnitRunner.getMainModuleInjector
    // Refactor and maintain a single method in RunnerUtils
    public Injector getMainModuleInjector() {
        //TODO: Synchronise this with e.g. synchronized (ZeroCodePackageRunner.class) {}
        final TargetEnv envAnnotation = testClass.getAnnotation(TargetEnv.class);
        String serverEnv = envAnnotation != null ? envAnnotation.value() : "config_hosts.properties";

        serverEnv = getEnvSpecificConfigFile(serverEnv, testClass);

        Class<? extends BasicHttpClient> runtimeHttpClient = createCustomHttpClientOrDefault();
        Class<? extends BasicKafkaClient> runtimeKafkaClient = createCustomKafkaClientOrDefault();

        return createInjector(Modules.override(new ApplicationMainModule(serverEnv))
                .with(
                        new RuntimeHttpClientModule(runtimeHttpClient),
                        new RuntimeKafkaClientModule(runtimeKafkaClient)
                ));
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

    public Class<? extends BasicKafkaClient> createCustomKafkaClientOrDefault() {
        final UseKafkaClient kafkaClientAnnotated = getUseKafkaClient();
        return kafkaClientAnnotated != null ? kafkaClientAnnotated.value() : ZerocodeCustomKafkaClient.class;
    }

    public Class<? extends BasicHttpClient> createCustomHttpClientOrDefault() {
        final UseHttpClient httpClientAnnotated = getUseHttpClient();
        return httpClientAnnotated != null ? httpClientAnnotated.value() : SslTrustHttpClient.class;
    }

    public UseHttpClient getUseHttpClient() {
        return testClass.getAnnotation(UseHttpClient.class);
    }

    public UseKafkaClient getUseKafkaClient() {
        return testClass.getAnnotation(UseKafkaClient.class);
    }

    private ZeroCodeMultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        zeroCodeMultiStepsScenarioRunner = getMainModuleInjector().getInstance(ZeroCodeMultiStepsScenarioRunner.class);
        return zeroCodeMultiStepsScenarioRunner;
    }

    private ZeroCodeReportGenerator getInjectedReportGenerator() {
        return getMainModuleInjector().getInstance(ZeroCodeReportGenerator.class);
    }

    private void handleNoRunListenerReport(RunListener reportListener) {
        handleTestCompleted(reportListener, LOGGER);
    }

    private List<String> readTestScenarioFiles() {
        // --------------------------------------------------
        // Backward compatibility
        // Warning - Stop supporting this for future releases
        // i.e. JsonTestCase
        // --------------------------------------------------
        List<JsonTestCase> jsonTestCases = Arrays.asList(testClass.getAnnotationsByType(JsonTestCase.class));
        if (jsonTestCases != null && jsonTestCases.size() > 0) {
            return jsonTestCases.stream()
                    .map(thisTestCase -> thisTestCase.value())
                    .collect(Collectors.toList());
        }

        List<Scenario> scenarios = Arrays.asList(testClass.getAnnotationsByType(Scenario.class));
        return scenarios.stream()
                .map(thisTestCase -> thisTestCase.value())
                .collect(Collectors.toList());
    }

    private void validateSuiteAnnotationPresent(TestPackageRoot rootPackageAnnotation,
                                                JsonTestCases jsonTestCasesAnnotation,
                                                Scenarios scenarios) {
        if (rootPackageAnnotation == null && (jsonTestCasesAnnotation == null && scenarios == null)) {
            throw new RuntimeException("Missing Test Suite details." +
                    "To run as a Test Suite - \n" +
                    "Annotate your Test Suite class with, e.g. \n@TestPackageRoot(\"resource_folder_for_scenario_files\") " +
                    "\n\n-Or- \n" +
                    "Annotate your Test Suite class with, e.g. \n@JsonTestCases({\n" +
                    "        @JsonTestCase(\"path/to/test_case_1.json\"),\n" +
                    "        @JsonTestCase(\"path/to/test_case_2.json\")\n" +
                    "})" +
                    "\n\n-Or- \n" +
                    "Annotate your Test Suite class with, e.g. \n@Scenarios({\n" +
                    "        @Scenario(\"path/to/test_case_1.json\"),\n" +
                    "        @Scenario(\"path/to/test_case_2.json\")\n" +
                    "})" +
                    "\n\n-Or- \n" +
                    "Run as usual 'Junit Suite' pointing to the individual test classes.");
        }
    }
}
