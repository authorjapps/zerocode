package org.jsmart.smarttester.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.smarttester.core.runner.SmartJUnitRunner;
import org.junit.runners.model.InitializationError;

public class TestOnlySmartJUnitRunner extends SmartJUnitRunner {
    SimpleRestJsonSimulatorsMain simulator ;
    public static final int PORT = 9999;

    public TestOnlySmartJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        simulator.start();
    }

}
