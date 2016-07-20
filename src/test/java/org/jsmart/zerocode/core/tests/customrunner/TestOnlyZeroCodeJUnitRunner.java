package org.jsmart.zerocode.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.zerocode.core.runner.ZeroCodeJUnitRunner;
import org.junit.runners.model.InitializationError;

public class TestOnlyZeroCodeJUnitRunner extends ZeroCodeJUnitRunner {
    SimpleRestJsonSimulatorsMain simulator ;
    public static final int PORT = 9999;

    public TestOnlyZeroCodeJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();
    }

}
