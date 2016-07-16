package org.jsmart.smarttester.core.runner;

import org.apache.catalina.LifecycleException;
import org.jsmart.smarttester.core.domain.TomcatWarProperties;
import org.jsmart.smarttester.core.listener.EmbeddedTomcatJunitListener;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroCodeTomcatSuiteRunner extends Suite {
    private static final Logger logger = LoggerFactory.getLogger(ZeroCodeTomcatSuiteRunner.class);

    private String tomcatContext;
    private String warLocation;
    private int port;

    public ZeroCodeTomcatSuiteRunner(Class<?> klass) throws InitializationError {
        super(klass, getAnnotatedClasses(klass));
    }

    private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
        SuiteClasses annotation = klass.getAnnotation(SuiteClasses.class);
        if (annotation == null) {
            throw new InitializationError(String.format("Class '%s' must have a SuiteClasses annotation", klass.getName()));
        }

        return annotation.value();
    }

    @Override
    protected TestClass createTestClass(Class<?> testClass) {
        TestClass suiteClass = super.createTestClass(testClass);
        TomcatWarProperties tomcatAnnotation = suiteClass.getAnnotation(TomcatWarProperties.class);
        tomcatContext = tomcatAnnotation.tomcatContext();
        warLocation = tomcatAnnotation.tomcatWarLocation();
        port = tomcatAnnotation.tomcatPort() > 0 ? tomcatAnnotation.tomcatPort() : 43210;

        return suiteClass;
    }


    @Override
    public void run(RunNotifier notifier) {
        for (Runner runner : getChildren()) {
            if (runner instanceof ZeroCodeTomcatUnitRunner) {
                ZeroCodeTomcatUnitRunner tomcatRunner = (ZeroCodeTomcatUnitRunner) runner;
                tomcatRunner.setPort(port);
                tomcatRunner.setWarLocation(warLocation);
                tomcatRunner.setContext(tomcatContext);
                tomcatRunner.setRunningViaSuite(true);
            }
        }
        logger.info("###Suite: Starting tomcat in run()...");

        EmbeddedTomcatJunitListener tomcatwithWebApp = new EmbeddedTomcatJunitListener(port, warLocation, tomcatContext);
        try {
            tomcatwithWebApp.startNow();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("###Suite: Error starting tomcat. Details: " + e);
        }

        super.run(notifier);

        try {
            tomcatwithWebApp.stopNow();
        } catch (LifecycleException e) {
            e.printStackTrace();
            throw new RuntimeException("###Suite: Error stopping tomcat. Details: " + e);
        }

        logger.info("###Suite: run() finished. Tomcat stopped.");
    }

}
