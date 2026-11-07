package org.jsmart.zerocode.core.di.main;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutor;
import org.jsmart.zerocode.core.engine.executor.ApiServiceExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.DelayExecutor;
import org.jsmart.zerocode.core.engine.executor.DelayExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.httpapi.HttpApiExecutor;
import org.jsmart.zerocode.core.engine.executor.httpapi.HttpApiExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaMethodExecutor;
import org.jsmart.zerocode.core.engine.executor.javaapi.JavaMethodExecutorImpl;

public class ApplicationMainModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApiServiceExecutor.class).to(ApiServiceExecutorImpl.class);
        bind(HttpApiExecutor.class).to(HttpApiExecutorImpl.class);
        bind(JavaMethodExecutor.class).to(JavaMethodExecutorImpl.class);
        bind(DelayExecutor.class).to(DelayExecutorImpl.class);
    }
}
