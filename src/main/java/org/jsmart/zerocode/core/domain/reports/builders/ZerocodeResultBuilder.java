package org.jsmart.zerocode.core.domain.reports.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.ZerocodeResult;

import java.util.ArrayList;
import java.util.List;

public class ZerocodeResultBuilder {
    private String scenarioName;
    private Integer loop;
    private List<ZeroCodeReportStep> steps = new ArrayList<>();

    public static ZerocodeResultBuilder newInstance() {
        return new ZerocodeResultBuilder();
    }

    public ZerocodeResult build() {
        ZerocodeResult built = new ZerocodeResult(scenarioName, loop, steps);
        return built;
    }

    public ZerocodeResultBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public ZerocodeResultBuilder loop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public ZerocodeResultBuilder steps(List<ZeroCodeReportStep> steps) {
        this.steps = steps;
        return this;
    }
}
