package org.jsmart.zerocode.core.report;

public interface ZeroCodeReportGenerator {
    void generateCsvReport();

    /**
     * Spike chat is disabled by default
     *
     */
    void generateHighChartReport();

    void generateExtentReport();
}
