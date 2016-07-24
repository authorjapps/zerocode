package org.jsmart.zerocode.core.domain.reports;

public class ZeroCodeCsvReport {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;
    private String correlationId;
    private String result;

    public ZeroCodeCsvReport(String scenarioName, Integer scenarioLoop, String stepName, Integer stepLoop, String correlationId, String result) {
        this.scenarioName = scenarioName;
        this.scenarioLoop = scenarioLoop;
        this.stepName = stepName;
        this.stepLoop = stepLoop;
        this.correlationId = correlationId;
        this.result = result;
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

    public String getCorrelationId() {
        return correlationId;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "ZeroCodeCsvReport{" +
                "scenarioName='" + scenarioName + '\'' +
                ", scenarioLoop=" + scenarioLoop +
                ", stepName='" + stepName + '\'' +
                ", stepLoop=" + stepLoop +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
