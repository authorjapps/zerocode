package org.jsmart.zerocode.core.domain.reports.csv;

public class ZeroCodeCsvReport {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;
    private String correlationId;
    private String result;
    private String method;
    String requestTimeStamp;
    String responseTimeStamp;
    private Double responseDelayMilliSec;
    // defining meta data fields
    private String metaAuthors;
    private String metaTickets;
    private String metaCategories;
    private String metaOthers;

    public ZeroCodeCsvReport(String scenarioName, Integer scenarioLoop, String stepName, Integer stepLoop,
                             String correlationId, String result, String method, String requestTimeStamp,
                             String responseTimeStamp, Double responseDelayMilliSec) {
        this.scenarioName = scenarioName;
        this.scenarioLoop = scenarioLoop;
        this.stepName = stepName;
        this.stepLoop = stepLoop;
        this.correlationId = correlationId;
        this.result = result;
        this.method=method;
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

    public String getMethod() {
        return method;
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

    // defining meta data field setters and getters

    public String getMetaAuthors() {
        return metaAuthors;
    }

    public void setMetaAuthors(String metaAuthors) {
        this.metaAuthors = metaAuthors;
    }

    public String getMetaTickets() {
        return metaTickets;
    }

    public void setMetaTickets(String metaTickets) {
        this.metaTickets = metaTickets;
    }

    public String getMetaCategories() {
        return metaCategories;
    }

    public void setMetaCategories(String metaCategories) {
        this.metaCategories = metaCategories;
    }

    public String getMetaOthers() {
        return metaOthers;
    }

    public void setMetaOthers(String metaOthers) {
        this.metaOthers = metaOthers;
    }

    @Override
    public String toString() {
        return "ZeroCodeCsvReport{" +
                "scenarioName='" + scenarioName + '\'' +
                ", scenarioLoop=" + scenarioLoop +
                ", stepName='" + stepName + '\'' +
                ", stepLoop=" + stepLoop +
                ", correlationId='" + correlationId + '\'' +
                ", requestTimeStamp='" + requestTimeStamp + '\'' +
                ", responseDelayMilliSec=" + responseDelayMilliSec +
                ", responseTimeStamp='" + responseTimeStamp + '\'' +
                ", result='" + result + '\'' +
                ", method='" + method + '\'' +
                ", metaAuthors='" + metaAuthors + '\'' +
                ", metaTickets='" + metaTickets + '\'' +
                ", metaCategories='" + metaCategories + '\'' +
                ", metaOthers='" + metaOthers + '\'' +
                '}';
    }
}
