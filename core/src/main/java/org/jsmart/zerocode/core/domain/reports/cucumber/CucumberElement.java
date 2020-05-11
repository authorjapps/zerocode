package org.jsmart.zerocode.core.domain.reports.cucumber;

public class CucumberElement {
    private final String name;
    private final String type;
    private final String description;
    private final String keyword;
    private final CucumberStep[] steps;

    public CucumberElement(String name, String type, String description, String keyword, CucumberStep[] steps) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.keyword = keyword;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getKeyword() {
        return keyword;
    }

    public CucumberStep[] getSteps() {
        return steps;
    }
}
