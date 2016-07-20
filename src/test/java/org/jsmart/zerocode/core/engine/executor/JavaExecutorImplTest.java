package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jsmart.zerocode.core.di.ApplicationMainModule;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JavaExecutorImplTest {

    JavaExecutor defaultJavaExecutor;
    Injector injector;
    SmartUtils smartUtils;
    ObjectMapper mapper;

    @Before
    public void name() throws Exception {
        injector = Guice.createInjector(new ApplicationMainModule("config_hosts_test.properties"));
        defaultJavaExecutor = new JavaExecutorImpl(injector);
        smartUtils = injector.getInstance(SmartUtils.class);
        mapper = smartUtils.getMapper();
    }

    @Test
    public void willExecuteA_Java_Method() throws Exception {

        final Object result = defaultJavaExecutor.execute("org.jsmart.zerocode.core.AddService", "add", 1, 2);
        assertThat(result, is(3));
    }

    @Test
    public void willExecuteJsonRequestFor_java_method_basics() throws Exception {
        String requestJson = "30";

        String serviceName = "org.jsmart.zerocode.core.AddService";
        String methodName = "square";

        List<Class<?>> argumentTypes = defaultJavaExecutor.argumentTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = defaultJavaExecutor.execute(serviceName, methodName, request);

        assertThat(result, is(900));
    }

    @Test
    public void willExecuteJsonRequestFor_java_method() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("05_test_java_service/01_test_json_java_service_method_Integer.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();
        List<Class<?>> argumentTypes = defaultJavaExecutor.argumentTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = defaultJavaExecutor.execute(serviceName, methodName, request);

        assertThat(result, is(65025));
    }
    @Test
    public void willExecuteJsonRequestFor_CustomObject_java_method() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("05_test_java_service/01_test_json_java_service_method_MyNumber.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();
        List<Class<?>> argumentTypes = defaultJavaExecutor.argumentTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = defaultJavaExecutor.execute(serviceName, methodName, request);

        assertThat(result, is(900));
    }

    @Test
    public void willExecuteJsonRequestFor_CustomObject_back_to_basics() throws Exception {
        String requestJson = "{\n" +
                "          \"number\": 30\n" +
                "        }";

        String serviceName = "org.jsmart.zerocode.core.AddService";
        String methodName = "squareMyNumber";

        List<Class<?>> argumentTypes = defaultJavaExecutor.argumentTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = defaultJavaExecutor.execute(serviceName, methodName, request);
        assertThat(result, is(900));
    }
}