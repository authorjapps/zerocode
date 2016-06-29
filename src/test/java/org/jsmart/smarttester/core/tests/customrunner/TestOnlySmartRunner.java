package org.jsmart.smarttester.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.smarttester.core.runner.SmartRunner;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runners.model.InitializationError;

public class TestOnlySmartRunner extends SmartRunner {
    SimpleRestJsonSimulatorsMain simulator ;
    public static final int PORT = 9999;

    public TestOnlySmartRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();
    }

    public TestOnlySmartRunner(Class<?> testClass, SmartUtils smartUtils) throws InitializationError {
        super(testClass, smartUtils);
    }

}
