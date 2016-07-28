package org.jsmart.zerocode.core.domain.reports.csv;

public class ZeroCodeCsvReport {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;
    private String correlationId;
    private String result;
    String requestTimeStamp;
    String responseTimeStamp;
    private Double responseDelayMilliSec;

    public ZeroCodeCsvReport(String scenarioName, Integer scenarioLoop, String stepName, Integer stepLoop,
                             String correlationId, String result, String requestTimeStamp,
                             String responseTimeStamp, Double responseDelayMilliSec) {
        this.scenarioName = scenarioName;
        this.scenarioLoop = scenarioLoop;
        this.stepName = stepName;
        this.stepLoop = stepLoop;
        this.correlationId = correlationId;
        this.result = result;
        this.requestTimeStamp = requestTimeStamp;
        this.responseTimeStamp = responseTimeStamp;
        this.responseDelayMilliSec = responseDelayMilliSec;
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

    public Double getResponseDelayMilliSec() {
        return responseDelayMilliSec;
    }

    public String getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public String getResponseTimeStamp() {
        return responseTimeStamp;
    }

    @Override
    public String toString() {
        return "ZeroCodeCsvReport{" +
                "scenarioName='" + scenarioName + '\'' +
                ", scenarioLoop=" + scenarioLoop +
                ", stepName='" + stepName + '\'' +
                ", stepLoop=" + stepLoop +
                ", correlationId='" + correlationId + '\'' +
                ", result='" + result + '\'' +
                ", requestTimeStamp=" + requestTimeStamp +
                ", responseTimeStamp=" + responseTimeStamp +
                ", responseDelayMilliSec=" + responseDelayMilliSec +
                '}';
    }
}
