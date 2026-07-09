package org.jsmart.zerocode.core.runner.retry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutor;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunnerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Mockito-based unit tests for the retry mechanism in ZeroCodeMultiStepsScenarioRunnerImpl.
 *
 * Uses a real Guice-wired runner with the ApiServiceExecutor replaced by a mock,
 * allowing controlled responses per call without requiring a running HTTP server.
 *
 * Covers:
 *  - Retry succeeds on a later attempt (stateful: fail then succeed)
 *  - Retry exhausts max attempts and the scenario fails
 */
public class RetryMockitoTest {

    private ZeroCodeMultiStepsScenarioRunnerImpl runner;
    private ApiServiceExecutor mockApiExecutor;
    private ObjectMapper objectMapper;
    private RunNotifier notifier;
    private Description description;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ApplicationMainModule("config_hosts_test.properties"));
        runner = injector.getInstance(ZeroCodeMultiStepsScenarioRunnerImpl.class);
        objectMapper = injector.getInstance(ObjectMapper.class);

        // Replace the Guice-injected ApiServiceExecutor with a Mockito mock via reflection
        mockApiExecutor = mock(ApiServiceExecutor.class);
        Field apiExecutorField = ZeroCodeMultiStepsScenarioRunnerImpl.class.getDeclaredField("apiExecutor");
        apiExecutorField.setAccessible(true);
        apiExecutorField.set(runner, mockApiExecutor);

        runner.overrideHost("http://localhost");
        runner.overridePort(9998);
        runner.overrideApplicationContext("");

        notifier = mock(RunNotifier.class);
        description = Description.createTestDescription(RetryMockitoTest.class, "test");
    }

    /**
     * Simulates a server that is unavailable on the first call (status 500)
     * and recovers by the second call (status 200).
     *
     * Verifies that:
     *  - the runner retries and the scenario ultimately passes
     *  - the executor was called exactly twice
     */
    @Test
    public void retrySucceedsOnSecondAttempt() throws Exception {
        when(mockApiExecutor.executeHttpApi(anyString(), anyString(), anyString()))
                .thenReturn("{\"status\": 500, \"body\": {}}")
                .thenReturn("{\"status\": 200, \"body\": {\"id\": 1}}");

        ScenarioSpec scenario = objectMapper.readValue(scenarioWithRetry(2), ScenarioSpec.class);

        boolean result = runner.runScenario(scenario, notifier, description);

        assertThat("scenario should pass after retry", result, is(true));
        verify(mockApiExecutor, times(2)).executeHttpApi(anyString(), anyString(), anyString());
    }

    /**
     * Simulates a server that never recovers — all calls return status 500.
     *
     * Verifies that:
     *  - the runner retries up to the configured max (3)
     *  - the scenario ultimately fails
     *  - the executor was called exactly 3 times
     */
    @Test
    public void retryExhaustsMaxAttemptsAndFails() throws Exception {
        when(mockApiExecutor.executeHttpApi(anyString(), anyString(), anyString()))
                .thenReturn("{\"status\": 500, \"body\": {}}");

        ScenarioSpec scenario = objectMapper.readValue(scenarioWithRetry(3), ScenarioSpec.class);

        boolean result = runner.runScenario(scenario, notifier, description);

        assertThat("scenario should fail after exhausting retries", result, is(false));
        verify(mockApiExecutor, times(3)).executeHttpApi(anyString(), anyString(), anyString());
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private String scenarioWithRetry(int maxRetries) {
        return "{\n" +
                "  \"scenarioName\": \"Retry scenario - max " + maxRetries + "\",\n" +
                "  \"steps\": [\n" +
                "    {\n" +
                "      \"name\": \"get_item\",\n" +
                "      \"url\": \"/retry/item\",\n" +
                "      \"operation\": \"GET\",\n" +
                "      \"retry\": {\"max\": " + maxRetries + ", \"delay\": 0},\n" +
                "      \"request\": {},\n" +
                "      \"assertions\": {\n" +
                "        \"status\": 200,\n" +
                "        \"body\": {\"id\": 1}\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

}
