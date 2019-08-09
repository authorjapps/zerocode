package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeExecResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZeroCodeExecReportBuilder {
    private String scenarioName;
    private Integer loop;
    private List<ZeroCodeReportStep> steps = Collections.synchronizedList(new ArrayList());

    public static ZeroCodeExecReportBuilder newInstance() {
        return new ZeroCodeExecReportBuilder();
    }

    public ZeroCodeExecResult build() {
        ZeroCodeExecResult built = new ZeroCodeExecResult(scenarioName, loop, steps);
        return built;
    }

    public ZeroCodeExecReportBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public ZeroCodeExecReportBuilder loop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public ZeroCodeExecReportBuilder steps(List<ZeroCodeReportStep> steps) {
        this.steps = steps;
        return this;
    }

    public ZeroCodeExecReportBuilder step(ZeroCodeReportStep step) {
        this.steps.add(step);
        return this;
    }
}
