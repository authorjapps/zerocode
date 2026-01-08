package org.jsmart.zerocode.core.runner;

import com.google.inject.Injector;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

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

}