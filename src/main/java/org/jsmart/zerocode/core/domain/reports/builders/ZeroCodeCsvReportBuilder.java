package org.jsmart.zerocode.core.domain.reports.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeCsvReport;

public class ZeroCodeCsvReportBuilder {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;
    private String correlationId;
    private String result;

    public static ZeroCodeCsvReportBuilder newInstance() {
        return new ZeroCodeCsvReportBuilder();
    }

    public ZeroCodeCsvReport build() {
        ZeroCodeCsvReport built = new ZeroCodeCsvReport(scenarioName,scenarioLoop,stepName, stepLoop, correlationId, result);
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
}
