package org.jsmart.zerocode.core.engine.executor;

import java.util.List;

public interface JavaExecutor {

	public Object execute(String qualifiedClassName, String methodName, Object... args);

	public List<Class<?>> argumentTypes(String className, String methodName);
}
