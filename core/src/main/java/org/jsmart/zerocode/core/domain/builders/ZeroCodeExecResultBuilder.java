package org.jsmart.zerocode.core.domain.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeExecResult;
import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;

public class ZeroCodeExecResultBuilder {
  private String scenarioName;
  private Integer loop;
  private List<ZeroCodeReportStep> steps = Collections.synchronizedList(new ArrayList());

  public static ZeroCodeExecResultBuilder newInstance() {
    return new ZeroCodeExecResultBuilder();
  }

  public ZeroCodeExecResult build() {
    ZeroCodeExecResult built = new ZeroCodeExecResult(scenarioName, loop, steps);
    return built;
  }

  public ZeroCodeExecResultBuilder scenarioName(String scenarioName) {
    this.scenarioName = scenarioName;
    return this;
  }

  public ZeroCodeExecResultBuilder loop(Integer loop) {
    this.loop = loop;
    return this;
  }

  public ZeroCodeExecResultBuilder steps(List<ZeroCodeReportStep> steps) {
    this.steps = steps;
    return this;
  }

  public ZeroCodeExecResultBuilder step(ZeroCodeReportStep step) {
    this.steps.add(step);
    return this;
  }
}
