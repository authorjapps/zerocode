package org.jsmart.zerocode.core.tests.customrunner;

public class SimulatorState {
    private static boolean started;

    public static boolean hasStarted() {
        return started;
    }

    public static void setStarted(boolean started) {
        SimulatorState.started = started;
    }


}
