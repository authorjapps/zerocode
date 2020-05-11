package org.jsmart.zerocode.core.domain.reports.cucumber;

public class CucumberStep {
    private final String keyword;
    private final CucumberResult result;
    private final String name;

    public CucumberStep(String keyword, CucumberResult result, String name) {
        this.keyword = keyword;
        this.result = result;
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public CucumberResult getResult() {
        return result;
    }

    public String getName() {
        return name;
    }
}
