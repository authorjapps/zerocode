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
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestReportListener;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.kafka.client.BasicKafkaClient;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.inject.Guice.createInjector;
import static java.lang.System.getProperty;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.CHARTS_AND_CSV;
import static org.jsmart.zerocode.core.constants.ZeroCodeReportConstants.ZEROCODE_JUNIT;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getCustomHttpClientOrDefault;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getCustomKafkaClientOrDefault;
import static org.jsmart.zerocode.core.utils.RunnerUtils.getEnvSpecificConfigFile;

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
     * @param child
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
        ZeroCodeTestReportListener reportListener = new ZeroCodeTestReportListener(smartUtils.getMapper(), getInjectedReportGenerator());
        notifier.addListener(reportListener);

        LOGGER.info("System property " + ZEROCODE_JUNIT + "=" + getProperty(ZEROCODE_JUNIT));
        if (!CHARTS_AND_CSV.equals(getProperty(ZEROCODE_JUNIT))) {
            notifier.addListener(reportListener);
        }

        super.run(notifier);

        handleNoRunListenerReport(reportListener);
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

        // ----------------------------------------------
        // Notify that this single test has been started.
        // Supply the scenario/journey name
        // ----------------------------------------------
        notifier.fireTestStarted(description);

        passed = zeroCodeMultiStepsScenarioRunner.runScenario(child, notifier, description);

        testRunCompleted = true;

        if (passed) {
            LOGGER.info(String.format("\nPackageRunner- **FINISHED executing all Steps for [%s] **.\nSteps were:%s",
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

        Class<? extends BasicHttpClient> runtimeHttpClient = getCustomHttpClientOrDefault(testClass);
        Class<? extends BasicKafkaClient> runtimeKafkaClient = getCustomKafkaClientOrDefault(testClass);

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

    private ZeroCodeMultiStepsScenarioRunner getInjectedMultiStepsRunner() {
        zeroCodeMultiStepsScenarioRunner = getMainModuleInjector().getInstance(ZeroCodeMultiStepsScenarioRunner.class);
        return zeroCodeMultiStepsScenarioRunner;
    }

    private ZeroCodeReportGenerator getInjectedReportGenerator() {
        return getMainModuleInjector().getInstance(ZeroCodeReportGenerator.class);
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
