package org.jsmart.zerocode.core.engine.executor.javaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.jsmart.zerocode.core.di.main.ApplicationMainModule;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.ScenarioSpec;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThrows;

public class JavaMethodExecutorImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    JavaMethodExecutorImpl methodExecutor;
    Injector injector;
    SmartUtils smartUtils;
    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        injector = Guice.createInjector(new ApplicationMainModule("config_hosts_test.properties"));
        mapper = new ObjectMapperProvider().get();

        methodExecutor = new JavaMethodExecutorImpl(injector, mapper);
        smartUtils = injector.getInstance(SmartUtils.class);
    }

    @Test
    public void willFind_matchingMethod() throws Exception {
        String serviceName = "org.jsmart.zerocode.core.AddService";
        String methodName = "squareMyNumber";

        Method matchingMethod = methodExecutor.findMatchingMethod(serviceName, methodName);

        assertThat(matchingMethod.getDeclaringClass().getName(), is("org.jsmart.zerocode.core.AddService"));
        assertThat(matchingMethod.getName(), is("squareMyNumber"));
    }

    @Test
    public void willExecuteA_Java_Method() throws Exception {

        final Object result = methodExecutor.executeWithParams("org.jsmart.zerocode.core.AddService", "add", 1, 2);
        assertThat(result, is(3));
    }

    @Test
    public void test_noMatchingMethod_exception() throws Exception {
        String serviceName = "org.jsmart.zerocode.core.AddService";
        String methodName = "invalidMethod";

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Java exec(): No matching method invalidMethod found in class org.jsmart.zerocode.core.AddService");
        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);
    }

    @Test
    public void willExecuteJsonRequestFor_javaMethod_viaReflection() throws Exception {
        String requestJson = "30";

        String serviceName = "org.jsmart.zerocode.core.AddService";
        String methodName = "square";

        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = methodExecutor.executeWithParams(serviceName, methodName, request);

        assertThat(result, is(900));
    }

    @Test
    public void willExecuteJsonRequestForJavaMethod_noParam() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("unit_test_files/java_apis/02_test_json_java_service_method_no_param.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();

        Object result = methodExecutor.executeWithParams(serviceName, methodName, (Object[]) null);

        assertThat(result, is(30));
    }

    @Test
    public void willExecuteJsonRequestFor_java_method_viaDsl() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("unit_test_files/java_apis/01_test_json_java_service_method_Integer.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();
        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = methodExecutor.executeWithParams(serviceName, methodName, request);

        assertThat(result, is(65025));
    }

    @Test
    public void willExecuteJsonRequestFor_CustomObject_java_method() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("unit_test_files/java_apis/01_test_json_java_service_method_MyNumber.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();
        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = methodExecutor.executeWithParams(serviceName, methodName, request);

        assertThat(result, is(900));
    }

    @Test
    public void willPropagateExceptions() throws Exception {
        String scenariosJsonAsString = SmartUtils.readJsonAsString("unit_test_files/java_apis/03_test_json_java_service_method_Exception.json");
        final ScenarioSpec scenarioSpec = smartUtils.getMapper().readValue(scenariosJsonAsString, ScenarioSpec.class);

        String serviceName = scenarioSpec.getSteps().get(0).getUrl();
        String methodName = scenarioSpec.getSteps().get(0).getOperation();
        String requestJson = scenarioSpec.getSteps().get(0).getRequest().toString();
        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	methodExecutor.executeWithParams(serviceName, methodName, request);
		});
        exception.printStackTrace();
        assertThat(exception.getMessage(), containsString("Invocation failed for method squareRoot"));
        // The target exception is included in this exception (inside of the InvocationTargetException)
        assertThat(exception.getCause(), is(notNullValue()));
        assertThat(((InvocationTargetException)exception.getCause()).getTargetException().getMessage(), 
        		is("Can not square root a negative number"));
    }

    @Test
    public void willExecuteJsonWithParams_CustomObject_viaJson() throws Exception {
        String requestJson = "{\n" +
                "          \"number\": 30\n" +
                "        }";

        String serviceName = "org.jsmart.zerocode.core.AddService";
        String methodName = "squareMyNumber";

        List<Class<?>> argumentTypes = methodExecutor.getParameterTypes(serviceName, methodName);

        Object request = mapper.readValue(requestJson, argumentTypes.get(0));
        Object result = methodExecutor.executeWithParams(serviceName, methodName, request);
        assertThat(result, is(900));
    }

}