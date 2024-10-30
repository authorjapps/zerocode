package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeExecResult;
import org.jsmart.zerocode.core.domain.ScenarioSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ZeroCodeExecReportBuilder {
    private String scenarioName;
    private Integer loop;
    private List<ZeroCodeReportStep> steps = Collections.synchronizedList(new ArrayList());
    private Map<String, List<String>> meta = ScenarioSpec.getMeta();

    public static ZeroCodeExecReportBuilder newInstance() {
        return new ZeroCodeExecReportBuilder();
    }

    public ZeroCodeExecResult build() {
        ZeroCodeExecResult built = new ZeroCodeExecResult(scenarioName, loop, steps, meta);
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
