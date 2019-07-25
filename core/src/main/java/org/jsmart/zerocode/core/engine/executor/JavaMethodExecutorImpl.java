package org.jsmart.zerocode.core.engine.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Class.forName;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.jsmart.zerocode.core.utils.SmartUtils.prettyPrintJson;

public class JavaMethodExecutorImpl implements JavaMethodExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMethodExecutorImpl.class);

    //guice
    private final Injector injector;

    private final ObjectMapper objectMapper;

    @Inject
    public JavaMethodExecutorImpl(Injector injector, ObjectMapper objectMapper) {
        this.injector = injector;
        this.objectMapper = objectMapper;
    }

    //guice

    @Override
    public String execute(String qualifiedClassName, String methodName, String requestJson) {

        try {
            List<Class<?>> parameterTypes = getParameterTypes(qualifiedClassName, methodName);

            Object result;

            if (parameterTypes == null || parameterTypes.size() == 0) {

                result = executeWithParams(qualifiedClassName, methodName);

            } else {

                Object request = objectMapper.readValue(requestJson, parameterTypes.get(0));
                result = executeWithParams(qualifiedClassName, methodName, request);
            }

            final String resultJson = objectMapper.writeValueAsString(result);
            return prettyPrintJson(resultJson);

        } catch (Exception e) {
            LOGGER.error("Exception - " + e);
            throw new RuntimeException(e);

        }
    }

    /*
     *
     * @param qualifiedClassName : including package name: e.g. "org.jsmart.zerocode.core.AddService"
     * @param methodName : public method in this class
     * @param params : parameters to this method
     * @return
     */
    Object executeWithParams(String qualifiedClassName, String methodName, Object... params) {

        /**
         * Refer SOF example:
         * Q. How do I invoke a Java method when given the method name as a string?
         * Link: https://stackoverflow.com/questions/160970/how-do-i-invoke-a-java-method-when-given-the-method-name-as-a-string
         */
        try {
            Method method = findMatchingMethod(qualifiedClassName, methodName);
            Object objectToInvokeOn = injector.getInstance(forName(qualifiedClassName));

            return method.invoke(objectToInvokeOn, params);
        } catch (Exception e) {
            String errMsg = format("Java exec(): Invocation failed for method %s in class %s", methodName, qualifiedClassName);
            LOGGER.error(errMsg + ". Exception - " + e);
            throw new RuntimeException(errMsg);
        }
    }

    Method findMatchingMethod(String className, String methodName) {
        try{
            // See the method invocation JDK documentation here:
            // Link: https://docs.oracle.com/javase/tutorial/reflect/member/methodInvocation.html
            // TODO - Handle overloaded methods e.g. Thread.sleep(args...)

            Class<?> clazz = forName(className);

            Method[] allMethods = clazz.getDeclaredMethods();
            for (Method m : allMethods) {
                if (m.getName().equals(methodName)) {
                    return m;
                }
            }

            throw new RuntimeException(format("Java exec(): No matching method %s found in class %s", methodName, className));

        } catch(Exception e){
            LOGGER.error("Exception occurred while finding the matching method - " + e);
            throw new RuntimeException(e);
        }
    }

    List<Class<?>> getParameterTypes(String className, String methodName) {
        return asList(findMatchingMethod(className, methodName).getParameterTypes());
    }

}
