package org.jsmart.zerocode.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.runners.model.InitializationError;

public class TestOnlyZeroCodeUnitRunner extends ZeroCodeUnitRunner {
    private static SimpleRestJsonSimulatorsMain simulator;
    public static final int PORT = 9998;

    static {
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();
    }

    public TestOnlyZeroCodeUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
}
