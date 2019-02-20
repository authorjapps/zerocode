package org.jsmart.zerocode.core.engine.executor;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class JavaExecutorImpl implements JavaExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaExecutorImpl.class);

    //guice
    private final Injector injector;

    @Inject
    public JavaExecutorImpl(Injector injector) {
        this.injector = injector;
    }
    //guice

    /*
     *
     * @param qualifiedClassName : including package name: e.g. "AddService"
     * @param methodName
     * @param args
     * @return
     */
    public Object execute(String qualifiedClassName, String methodName, Object... args) {

        try {

            return findMethod(qualifiedClassName, methodName)
                    .invoke(injector.getInstance(getClass(qualifiedClassName)), args);
        } catch (Exception e) {
            LOGGER.error("Exception encountered while executing java method" + e);
            throw new RuntimeException(e);
        }
    }

    public List<Class<?>> argumentTypes(String className, String methodName) {
        return Arrays.asList(findMethod(className, methodName).getParameterTypes());
    }

    private Method findMethod(String className, String methodName) {
        Class<?> type = getClass(className);

        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method declared : declaredMethods) {
            if (declared.getName().equals(methodName)) {
                return declared;
            }
        }

        throw new RuntimeException(String.format("Java: Could not find method %s in class %s", methodName, className));

    }

    private Class<?> getClass(String className) {
        Class<?> type;
        try {
            type = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return type;
    }

}

