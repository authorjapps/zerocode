package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberResult;
import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberStep;

public class ZeroCodeCucumberStepBuilder {
    private String keyword;
    private CucumberResult result;
    private String name;

    public static ZeroCodeCucumberStepBuilder newInstance() {
        return new ZeroCodeCucumberStepBuilder();
    }

    public CucumberStep build() {
        return new CucumberStep(keyword, result, name);
    }

    public ZeroCodeCucumberStepBuilder keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public ZeroCodeCucumberStepBuilder result(CucumberResult result) {
        this.result = result;
        return this;
    }

    public ZeroCodeCucumberStepBuilder name(String name) {
        this.name = name;
        return this;
    }
}
