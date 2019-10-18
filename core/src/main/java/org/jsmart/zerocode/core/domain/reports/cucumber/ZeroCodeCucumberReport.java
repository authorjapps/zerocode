package org.jsmart.zerocode.core.domain.reports.cucumber;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeroCodeCucumberReport {
    private final String id;
    private final String name;
    // as long as this is private attribute without getter deserialization must be forced by annotation
    @JsonProperty("uri")
    private final String uri;
    private final String description;
    private final String keyword;

    private final CucumberElement[] elements;
    //private final Tag[] tags = new Tag[0];
    // End: attributes from JSON file report

    private final String reportFileName;

    public ZeroCodeCucumberReport(String id, String name, String uri, String description, String keyword, CucumberElement[] elements, String reportFileName) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.description = description;
        this.keyword = keyword;
        this.elements = elements;
        this.reportFileName = reportFileName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

    public String getKeyword() {
        return keyword;
    }

    public CucumberElement[] getElements() {
        return elements;
    }

    public String getReportFileName() {
        return reportFileName;
    }
}
