package org.jsmart.zerocode.core.engine.executor.javaapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JavaCustomExecutor {

    public static Boolean executeMethod(String fqMethodNameWithRawParams, Object actualFieldValue) {
        ObjectMapper mapper = new ObjectMapperProvider().get();
        try {
            // -------------------
            // Find the class name
            // -------------------
            String[] parts = fqMethodNameWithRawParams.split("#");
            String className = parts[0];

            // -------------------
            // Find the method name
            // -------------------
            String methodNameWithVal = parts[1];
            String[] methodParts = methodNameWithVal.split(":", 2);
            String methodName = methodParts[0];

            // ---------------------
            // Find method arguments
            // ---------------------
            String expectedRawJsonArgs = methodParts[1];
            HashMap<String, Object> valueMap = mapper.readValue(expectedRawJsonArgs, HashMap.class);

            // -------------------
            // Invoke
            // -------------------
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, Map.class, Object.class);
            // ----------------------------------------------------
            // For static method exec, invoke(clazz, param1, ...);
            // clazz.newInstance() not required
            // ----------------------------------------------------
            Object result = method.invoke(clazz.newInstance(), valueMap, actualFieldValue);

            return (Boolean) result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
