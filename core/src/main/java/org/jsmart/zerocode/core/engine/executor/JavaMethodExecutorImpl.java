package org.jsmart.zerocode.core.engine.executor;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Class.forName;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class JavaMethodExecutorImpl implements JavaMethodExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMethodExecutorImpl.class);

    //guice
    private final Injector injector;

    @Inject
    public JavaMethodExecutorImpl(Injector injector) {
        this.injector = injector;
    }
    //guice

    /*
     *
     * @param qualifiedClassName : including package name: e.g. "org.jsmart.zerocode.core.AddService"
     * @param methodName
     * @param params
     * @return
     */
    public Object execute(String qualifiedClassName, String methodName, Object... params) {

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

    public List<Class<?>> getParameterTypes(String className, String methodName) {
        return asList(findMatchingMethod(className, methodName).getParameterTypes());
    }

    private Method findMatchingMethod(String className, String methodName) {
        try{
            // See the method invocation JDK documentation here:
            // Link: https://docs.oracle.com/javase/tutorial/reflect/member/methodInvocation.html
            // TODO - Handle overloaded method i.e. Thread.sleep(args...) types

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
}
