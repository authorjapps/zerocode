package org.jsmart.zerocode.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import java.util.Map;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaMethodExecutorImpl;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class DelayExecutorTest {

    JavaMethodExecutorImpl methodExecutor;
    ObjectMapper mapper;
    SmartUtils smartUtils;

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new ApplicationMainModule("config_hosts_test.properties"));
        mapper = new ObjectMapperProvider().get();
        methodExecutor = new JavaMethodExecutorImpl(injector, mapper);
        smartUtils = injector.getInstance(SmartUtils.class);
    }

    @Test
    public void willDelayForRequestedMillisAndReturnSuccess() throws Exception {
        long start = System.currentTimeMillis();
        Map<String, Object> result = DelayExecutor.DELAY(50L);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(result.get("status"), is("Success"));
        assertThat(result.get("delayMillis"), is(50L));
        assertTrue("Expected at least 50ms delay, was " + elapsed, elapsed >= 50);
    }

    @Test
    public void willReturnImmediatelyForZeroDelay() throws Exception {
        Map<String, Object> result = DelayExecutor.DELAY(0L);
        assertThat(result.get("status"), is("Success"));
        assertThat(result.get("delayMillis"), is(0L));
    }

    @Test
    public void willRejectNegativeDelay() {
        assertThrows(IllegalArgumentException.class, () -> DelayExecutor.DELAY(-1L));
    }

    @Test
    public void willRejectNullDelay() {
        assertThrows(IllegalArgumentException.class, () -> DelayExecutor.DELAY(null));
    }

    @Test
    public void willResolveUtilProtocolMappingToDelayExecutor() {
        ApiTypeUtils apiTypeUtils = new ApiTypeUtils("util://DelayExecutor|org.jsmart.zerocode.core.utils.DelayExecutor");
        assertThat(apiTypeUtils.getQualifiedJavaApi("util://DelayExecutor"),
                is("org.jsmart.zerocode.core.utils.DelayExecutor"));
    }

    @Test
    public void willExecuteDelayViaScenarioDsl() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("unit_test_files/java_apis/04_test_json_java_service_delay.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();
        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = methodExecutor.executeWithParams(serviceName, methodName, request);

        assertThat(result, is(Map.of("status", "Success", "delayMillis", 50L)));
    }
}
