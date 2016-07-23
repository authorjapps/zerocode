package org.jsmart.zerocode.core.domain.reports.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeCsvReport;

public class ZeroCodeCsvReportBuilder {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;

    public static ZeroCodeCsvReportBuilder newInstance() {
        return new ZeroCodeCsvReportBuilder();
    }

    public ZeroCodeCsvReport build() {
        ZeroCodeCsvReport built = new ZeroCodeCsvReport(scenarioName,scenarioLoop,stepName, stepLoop);
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


}
