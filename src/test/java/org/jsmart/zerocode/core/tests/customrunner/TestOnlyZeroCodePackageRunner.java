package org.jsmart.zerocode.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.zerocode.core.runner.ZeroCodePackageRunner;
import org.jsmart.zerocode.core.utils.SmartUtils;
import org.junit.runners.model.InitializationError;

public class TestOnlyZeroCodePackageRunner extends ZeroCodePackageRunner {
    SimpleRestJsonSimulatorsMain simulator ;
    public static final int PORT = 9998;

    public TestOnlyZeroCodePackageRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();
    }

    public TestOnlyZeroCodePackageRunner(Class<?> testClass, SmartUtils smartUtils) throws InitializationError {
        super(testClass, smartUtils);
    }

}
