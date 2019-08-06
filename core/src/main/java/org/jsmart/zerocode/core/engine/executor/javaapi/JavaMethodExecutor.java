package org.jsmart.zerocode.core.engine.executor.javaapi;

public interface JavaMethodExecutor {
    String execute(String qualifiedClassName, String methodName, String requestJson);
}
