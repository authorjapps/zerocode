package org.jsmart.zerocode.core.engine.executor;

import java.util.List;

public interface JavaMethodExecutor {
    Object execute(String qualifiedClassName, String methodName, Object... args);
    List<Class<?>> getParameterTypes(String className, String methodName);
}
