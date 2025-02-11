package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.csv.ZeroCodeCsvReport;

public class ZeroCodeCsvReportBuilder {
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
    private String metaAuthors;
    private String metaTickets;
    private String metaCategories;
    private String metaOthers;

    public static ZeroCodeCsvReportBuilder newInstance() {
        return new ZeroCodeCsvReportBuilder();
    }

    public ZeroCodeCsvReport build() {
        ZeroCodeCsvReport built = new ZeroCodeCsvReport(scenarioName,scenarioLoop,stepName, stepLoop,
                correlationId, result, method, requestTimeStamp, responseTimeStamp, responseDelayMilliSec);
        built.setMetaAuthors(metaAuthors);
        built.setMetaTickets(metaTickets);
        built.setMetaCategories(metaCategories);
        built.setMetaOthers(metaOthers);
        return built;
    }

    public ZeroCodeCsvReportBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public ZeroCodeCsvReportBuilder scenarioLoop(Integer scenarioLoop) {
        this.scenarioLoop = scenarioLoop;
        return this;
    }

    public ZeroCodeCsvReportBuilder stepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    public ZeroCodeCsvReportBuilder stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public ZeroCodeCsvReportBuilder correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public ZeroCodeCsvReportBuilder result(String result) {
        this.result = result;
        return this;
    }

    public ZeroCodeCsvReportBuilder method(String method) {
        this.method = method;
        return this;
    }

    public ZeroCodeCsvReportBuilder requestTimeStamp(String requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
        return this;
    }

    public ZeroCodeCsvReportBuilder responseTimeStamp(String responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public ZeroCodeCsvReportBuilder responseDelayMilliSec(Double responseDelayMilliSec) {
        this.responseDelayMilliSec = responseDelayMilliSec;
        return this;
    }

    public ZeroCodeCsvReportBuilder setMetaAuthors(String metaAuthors) {
        this.metaAuthors = metaAuthors;
        return this;
    }

    public ZeroCodeCsvReportBuilder setMetaTickets(String metaTickets) {
        this.metaTickets = metaTickets;
        return this;
    }

    public ZeroCodeCsvReportBuilder setMetaCategories(String metaCategories) {
        this.metaCategories = metaCategories;
        return this;
    }

    public ZeroCodeCsvReportBuilder setMetaOthers(String metaOthers) {
        this.metaOthers = metaOthers;
        return this;
    }
}
