package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberElement;
import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberStep;

public class ZeroCodeCucumberElementBuilder {
    private String name;
    private String type;
    private String description;
    private String keyword;
    private CucumberStep[] steps;

    public static ZeroCodeCucumberElementBuilder newInstance() {
        return new ZeroCodeCucumberElementBuilder();
    }

    public CucumberElement build() {
        return new CucumberElement(name, type, description, keyword, steps);
    }


    public ZeroCodeCucumberElementBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ZeroCodeCucumberElementBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ZeroCodeCucumberElementBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ZeroCodeCucumberElementBuilder keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public ZeroCodeCucumberElementBuilder steps(CucumberStep[] steps) {
        this.steps = steps;
        return this;
    }
}
