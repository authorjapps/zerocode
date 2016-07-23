package org.jsmart.zerocode.core.domain.reports.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.ZerocodeResult;

import java.util.ArrayList;
import java.util.List;

public class ZeroCodeResultBuilder {
    private String scenarioName;
    private Integer loop;
    private List<ZeroCodeReportStep> steps = new ArrayList<>();

    public static ZeroCodeResultBuilder newInstance() {
        return new ZeroCodeResultBuilder();
    }

    public ZerocodeResult build() {
        ZerocodeResult built = new ZerocodeResult(scenarioName, loop, steps);
        return built;
    }

    public ZeroCodeResultBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public ZeroCodeResultBuilder loop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public ZeroCodeResultBuilder steps(List<ZeroCodeReportStep> steps) {
        this.steps = steps;
        return this;
    }

    public ZeroCodeResultBuilder step(ZeroCodeReportStep step) {
        this.steps.add(step);
        return this;
    }
}
