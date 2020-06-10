package org.jsmart.zerocode.core.tests.customrunner;

import org.jsmart.simulator.main.SimpleRestJsonSimulatorsMain;
import org.jsmart.zerocode.core.httpclient.BasicHttpClient;
import org.jsmart.zerocode.core.httpclient.ssl.SslTrustHttpClient;
import org.jsmart.zerocode.core.runner.ZeroCodeUnitRunner;
import org.junit.runners.model.InitializationError;

public class TestOnlyZeroCodeUnitRunner extends ZeroCodeUnitRunner {
    private static SimpleRestJsonSimulatorsMain simulator;
    public static final int PORT = 9998;

    static {

        System.setProperty("env_property_key_name", "ci"); //<--- See log n verify

        simulator = new SimpleRestJsonSimulatorsMain(PORT);
        if(!SimulatorState.hasStarted()){
            simulator.start();
            SimulatorState.setStarted(true);
        }
    }

    public TestOnlyZeroCodeUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    public Class<? extends BasicHttpClient> createCustomHttpClientOrDefault() {
        return getUseHttpClient() == null? SslTrustHttpClient.class : getUseHttpClient().value();
    }

}
