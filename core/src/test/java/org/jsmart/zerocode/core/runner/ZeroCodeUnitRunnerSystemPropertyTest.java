package org.jsmart.zerocode.core.runner;

import com.google.inject.Injector;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for behaviors added around system property overrides in ZeroCodeUnitRunner.
 * <p>
 * - Tests that zerocode.scenario system property overrides test-case names returned by getSmartChildrenList.
 * - Tests that runChild uses the zerocode.scenario override to load the scenario and invoke the multi-steps runner.
 */
public class ZeroCodeUnitRunnerSystemPropertyTest {

    // Dummy test class used to create FrameworkMethod instances for the runner
    public static class DummyTestClass {
        @JsonTestCase("/abcd/path")
        @Test
        public void dummyTestMethod() {
            // no-op
        }
    }

    @After
    public void tearDown() {
        // clear any system properties we set during tests to avoid side effects
        System.clearProperty("zerocode.scenario");
        System.clearProperty("zerocode.env");
    }

    @Test
    public void getSmartChildrenList_willGetScenarioFileFrom_SystemProperties() throws Exception {
        // Set the scenario file in System properties
        System.setProperty("zerocode.scenario", "OVERRIDE_SCENARIO_FILE.json");

        // Provide a SmartUtils stub so constructor doesn't attempt to create a real injector
        SmartUtils mockSmartUtils = mock(SmartUtils.class);

        // Also create a dummy injector to satisfy any calls, though our getInjectedSmartUtilsClass override avoids injector usage
        Injector mockInjector = mock(Injector.class);

        // Instantiate a ZeroCodeUnitRunner that returns our mocked SmartUtils from getInjectedSmartUtilsClass
        ZeroCodeUnitRunner runner = new ZeroCodeUnitRunner(DummyTestClass.class) {
            @Override
            protected SmartUtils getInjectedSmartUtilsClass() {
                return mockSmartUtils;
            }

            // Ensure any calls to getMainModuleInjector (e.g. during createZeroCodeMultiStepRunner) receive a mock
            @Override
            public Injector getMainModuleInjector() {
                return mockInjector;
            }
        };

        // Act
        List<String> jsonScenarios = runner.getSmartTestCaseNames();

        // Assert - DummyTestClass has one method 'dummyTestMethod' and system override should replace it
        assertNotNull(jsonScenarios);
        assertEquals(1, jsonScenarios.size());
        assertEquals("OVERRIDE_SCENARIO_FILE.json", jsonScenarios.get(0));
    }

    @Test
    public void runChild_usesScenarioOverrideJsonFile_and_invokesMultiStepsRunner() throws Exception {
        // Set the file in System properties
        String overrideScenarioFileJson = "OVERRIDE_SCENARIO";
        System.setProperty("zerocode.scenario", overrideScenarioFileJson);

        // Mock SmartUtils to return a ScenarioSpec for the override scenario
        SmartUtils mockSmartUtils = mock(SmartUtils.class);
        ScenarioSpec mockScenarioSpec = mock(ScenarioSpec.class);
        when(mockSmartUtils.scenarioFileToJava(eq(overrideScenarioFileJson), eq(ScenarioSpec.class))).thenReturn(mockScenarioSpec);

        // Mock the multi-steps runner and injector so createZeroCodeMultiStepRunner() ends up wiring our mock
        ZeroCodeMultiStepsScenarioRunner mockMultiStepsRunner = mock(ZeroCodeMultiStepsScenarioRunner.class);
        when(mockMultiStepsRunner.runScenario(eq(mockScenarioSpec), any(RunNotifier.class), any(Description.class)))
                .thenReturn(true);

        Injector mockInjector = mock(Injector.class);
        // When the runner asks the injector for ZeroCodeMultiStepsScenarioRunner, give it our mock
        when(mockInjector.getInstance(ZeroCodeMultiStepsScenarioRunner.class)).thenReturn(mockMultiStepsRunner);

        // Create the runner overriding only the parts that would otherwise create real dependencies
        ZeroCodeUnitRunner runner = new ZeroCodeUnitRunner(DummyTestClass.class) {
            @Override
            protected SmartUtils getInjectedSmartUtilsClass() {
                return mockSmartUtils;
            }

            @Override
            public Injector getMainModuleInjector() {
                return mockInjector;
            }
        };

        // Prepare a FrameworkMethod for DummyTestClass#dummyTestMethod
        Method dummyMethod = DummyTestClass.class.getMethod("dummyTestMethod");
        FrameworkMethod frameworkMethod = new FrameworkMethod(dummyMethod);

        // RunNotifier mock to verify that test start/finish are invoked
        RunNotifier mockNotifier = mock(RunNotifier.class);

        // Run the test
        runner.runChild(frameworkMethod, mockNotifier);

        // Assert
        // SmartUtils should have been called to load the scenario with the override name
        verify(mockSmartUtils).scenarioFileToJava(eq(overrideScenarioFileJson), eq(ScenarioSpec.class));

        // The multi-steps runner should be invoked with the ScenarioSpec returned by SmartUtils
        verify(mockMultiStepsRunner).runScenario(eq(mockScenarioSpec), any(RunNotifier.class), any(Description.class));

        // The notifier should have been asked to start and finish the test
        // (At least verify fireTestStarted and fireTestFinished were called at some point)
        verify(mockNotifier, atLeastOnce()).fireTestStarted(any(Description.class));
        verify(mockNotifier, atLeastOnce()).fireTestFinished(any(Description.class));
    }
}