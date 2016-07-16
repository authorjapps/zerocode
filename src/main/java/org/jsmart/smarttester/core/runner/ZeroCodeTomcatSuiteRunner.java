package org.jsmart.smarttester.core.runner;

import org.apache.catalina.LifecycleException;
import org.jsmart.smarttester.core.listener.EmbeddedTomcatJunitListener;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeroCodeTomcatSuiteRunner extends Suite {
    private static final Logger logger = LoggerFactory.getLogger(ZeroCodeTomcatSuiteRunner.class);

    protected ZeroCodeTomcatSuiteRunner(Class<?> klass, Class<?>[] suiteClasses)
            throws InitializationError {
        super(klass, suiteClasses);
    }

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
    public void run(RunNotifier notifier) {
        for (Runner runner : getChildren()) {
            if (runner instanceof ZeroCodeTomcatUnitRunner) {
                ZeroCodeTomcatUnitRunner tomcatRunner = (ZeroCodeTomcatUnitRunner) runner;
//                tomcatRunner.setPort(port);
//                tomcatRunner.setWarLocation(warLocation);
//                tomcatRunner.setContext(tomcatContext);
                tomcatRunner.setRunningViaSuite(true);
            }
        }
        logger.info("###Suite: Starting tomcat in run()...............");

        EmbeddedTomcatJunitListener tomcatwithWebApp = new EmbeddedTomcatJunitListener(43210, "target/wars", "/ipt-ss-address-management-services");
        try {
            tomcatwithWebApp.startNow();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("###Suite: Error starting tomcat. Details: " + e);
        }


//        notifier.fireTestRunStarted(getDescription());
        super.run(notifier);
//        notifier.fireTestFinished(getDescription());

        try {
            tomcatwithWebApp.stopNow();
        } catch (LifecycleException e) {
            e.printStackTrace();
            throw new RuntimeException("###Suite:Error starting tomcat. Details: " + e);
        }

        logger.info("Suite: ### run() finished. Tomcat stopped.");
    }

}
