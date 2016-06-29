package org.jsmart.smarttester.core.di;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.jsmart.smarttester.core.engine.executor.JavaExecutorImpl;
import org.jsmart.smarttester.core.engine.executor.JavaExecutor;
import org.jsmart.smarttester.core.engine.executor.JsonServiceExecutor;
import org.jsmart.smarttester.core.engine.executor.JsonServiceExecutorImpl;
import org.jsmart.smarttester.core.engine.preprocessor.JsonTestProcesor;
import org.jsmart.smarttester.core.engine.preprocessor.JsonTestProcesorImpl;
import org.jsmart.smarttester.core.runner.MultiStepsScenarioRunnerImpl;
import org.jsmart.smarttester.core.runner.MultiStepsScenarioRunner;

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

        /*
         * Bind Direct classes, classes to interfaces etc
         */
        bind(MultiStepsScenarioRunner.class).to(MultiStepsScenarioRunnerImpl.class);
        bind(JsonServiceExecutor.class).to(JsonServiceExecutorImpl.class);
        bind(JavaExecutor.class).to(JavaExecutorImpl.class);
        bind(JsonTestProcesor.class).to(JsonTestProcesorImpl.class);
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
