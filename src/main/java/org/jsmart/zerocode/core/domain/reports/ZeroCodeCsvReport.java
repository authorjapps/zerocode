package org.jsmart.zerocode.core.domain.reports;

public class ZeroCodeCsvReport {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;

    public ZeroCodeCsvReport(String scenarioName, Integer scenarioLoop, String stepName, Integer stepLoop) {
        this.scenarioName = scenarioName;
        this.scenarioLoop = scenarioLoop;
        this.stepName = stepName;
        this.stepLoop = stepLoop;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public Integer getScenarioLoop() {
        return scenarioLoop;
    }

    public String getStepName() {
        return stepName;
    }

    public Integer getStepLoop() {
        return stepLoop;
    }

    @Override
    public String toString() {
        return "ZeroCodeCsvReport{" +
                "scenarioName='" + scenarioName + '\'' +
                ", scenarioLoop=" + scenarioLoop +
                ", stepName='" + stepName + '\'' +
                ", stepLoop=" + stepLoop +
                '}';
    }
}
