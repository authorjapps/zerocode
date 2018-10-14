package org.jsmart.zerocode.core.di;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jsmart.zerocode.core.engine.executor.JavaExecutor;
import org.jsmart.zerocode.core.engine.executor.JavaExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutor;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutorImpl;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeExternalFileProcessorImpl;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesorImpl;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunner;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunnerImpl;

import java.util.Properties;
import java.util.logging.Logger;

public class ApplicationMainModule extends AbstractModule {
    private static final Logger LOGGER = Logger.getLogger(ApplicationMainModule.class.getName());

    private final String serverEnv;
    private final String WEB_APPLICATION_ENDPOINT_HOST="web.application.endpoint.host";
    private final String WEB_APPLICATION_ENDPOINT_PORT="web.application.endpoint.port";
    private final String WEB_APPLICATION_ENDPOINT_CONTEXT="web.application.endpoint.context";
    private final String RESTFUL_APPLICATION_ENDPOINT_HOST="restful.application.endpoint.host";
    private final String RESTFUL_APPLICATION_ENDPOINT_PORT="restful.application.endpoint.port";
    private final String RESTFUL_APPLICATION_ENDPOINT_CONTEXT="restful.application.endpoint.context";


    public ApplicationMainModule(String serverEnv) {
        this.serverEnv = serverEnv;
    }


    @Override
    public void configure() {
        /*
         * Install other guice modules
         */
        install(new ObjectMapperModule());
        install(new HttpClientModule());

        /*
         * Bind Direct classes, classes to interfaces etc
         */
        bind(ZeroCodeMultiStepsScenarioRunner.class).to(ZeroCodeMultiStepsScenarioRunnerImpl.class);
        bind(JsonServiceExecutor.class).to(JsonServiceExecutorImpl.class);
        bind(JavaExecutor.class).to(JavaExecutorImpl.class);
        bind(ZeroCodeJsonTestProcesor.class).to(ZeroCodeJsonTestProcesorImpl.class);
        bind(ZeroCodeReportGenerator.class).to(ZeroCodeReportGeneratorImpl.class);
        bind(ZeroCodeExternalFileProcessor.class).to(ZeroCodeExternalFileProcessorImpl.class);
        //bind(ExecutorServiceRunner.class).in(Singleton.class);
        //bind(SmartUtils.class);

        /*
		 * Bind properties for localhost, CI, PRE-PROD etc
		 */
        Names.bindProperties(binder(), getProperties(serverEnv));
    }

    public Properties getProperties(String host) {
        final Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(host));
            /// below code is for backward compatibility, remove this after few releases ///
            if(properties.get(WEB_APPLICATION_ENDPOINT_HOST) == null){
                properties.setProperty(WEB_APPLICATION_ENDPOINT_HOST, (String)properties.get(RESTFUL_APPLICATION_ENDPOINT_HOST));
            }
            if(properties.get(WEB_APPLICATION_ENDPOINT_PORT) == null){
                properties.setProperty(WEB_APPLICATION_ENDPOINT_PORT, (String)properties.get(RESTFUL_APPLICATION_ENDPOINT_PORT));
            }
            if(properties.get(WEB_APPLICATION_ENDPOINT_CONTEXT) == null){
                properties.setProperty(WEB_APPLICATION_ENDPOINT_CONTEXT, (String)properties.get(RESTFUL_APPLICATION_ENDPOINT_CONTEXT));
            }
        } catch (Exception e) {
            LOGGER.info("###Oops!Exception### while reading target env file: " + host + ". Have you mentioned env details?");
            e.printStackTrace();

            throw new RuntimeException("could not read the target-env properties file --" + host + "-- from the classpath.");
        }

        return properties;
    }

}
