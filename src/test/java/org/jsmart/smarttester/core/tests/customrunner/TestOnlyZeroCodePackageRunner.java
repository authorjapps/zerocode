package org.jsmart.smarttester.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.smarttester.core.runner.ZeroCodePackageRunner;
import org.jsmart.smarttester.core.utils.SmartUtils;
import org.junit.runners.model.InitializationError;

public class TestOnlyZeroCodePackageRunner extends ZeroCodePackageRunner {
    SimpleRestJsonSimulatorsMain simulator ;
    public static final int PORT = 9999;

    public TestOnlyZeroCodePackageRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();
    }

    public TestOnlyZeroCodePackageRunner(Class<?> testClass, SmartUtils smartUtils) throws InitializationError {
        super(testClass, smartUtils);
    }

}
