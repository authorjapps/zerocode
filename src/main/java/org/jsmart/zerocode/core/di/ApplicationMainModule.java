package org.jsmart.zerocode.core.di;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jsmart.zerocode.core.engine.executor.JavaExecutorImpl;
import org.jsmart.zerocode.core.engine.executor.JavaExecutor;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutor;
import org.jsmart.zerocode.core.engine.executor.JsonServiceExecutorImpl;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesor;
import org.jsmart.zerocode.core.engine.preprocessor.ZeroCodeJsonTestProcesorImpl;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunnerImpl;
import org.jsmart.zerocode.core.runner.ZeroCodeMultiStepsScenarioRunner;

import java.util.Properties;
import java.util.logging.Logger;

public class ApplicationMainModule extends AbstractModule {
    private static final Logger LOGGER = Logger.getLogger(ApplicationMainModule.class.getName());

    private final String serverEnv;

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

        } catch (Exception e) {
            LOGGER.info("###Oops!Exception### while reading target env file: " + host + ". Have you mentioned env details?");
            e.printStackTrace();

            throw new RuntimeException("could not read the target-env properties file --" + host + "-- from the classpath.");
        }

        return properties;
    }

}
