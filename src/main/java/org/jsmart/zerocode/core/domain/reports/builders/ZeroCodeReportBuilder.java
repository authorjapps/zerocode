package org.jsmart.zerocode.core.domain.reports.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReport;
import org.jsmart.zerocode.core.domain.reports.ZerocodeResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ZeroCodeReportBuilder {
    private LocalDateTime timeStamp;
    private List<ZerocodeResult> results = new ArrayList<ZerocodeResult>();

    public static ZeroCodeReportBuilder newInstance() {
        return new ZeroCodeReportBuilder();
    }

    public ZeroCodeReport build() {
        ZeroCodeReport built = new ZeroCodeReport(timeStamp, results);
        return built;
    }

    public ZeroCodeReportBuilder timeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public ZeroCodeReportBuilder results(List<ZerocodeResult> results) {
        this.results = results;
        return this;
    }
}
