package org.jsmart.zerocode.core.domain.builders;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jsmart.zerocode.core.domain.reports.cucumber.CucumberElement;
import org.jsmart.zerocode.core.domain.reports.cucumber.ZeroCodeCucumberReport;

public class ZeroCodeCucumberReportBuilder {
    private String id;
    private String name;
    // as long as this is private attribute without getter deserialization must be forced by annotation
    @JsonProperty("uri")
    private String uri;
    private String description;
    private String keyword;

    private CucumberElement[] cucumberElements;
    //private final Tag[] tags = new Tag[0];
    // End: attributes from JSON file report

    private String reportFileName;

    public static ZeroCodeCucumberReportBuilder newInstance() {
        return new ZeroCodeCucumberReportBuilder();
    }

    public ZeroCodeCucumberReport build() {
        return new ZeroCodeCucumberReport(id, name, uri, description, keyword, cucumberElements, reportFileName);
    }

    public ZeroCodeCucumberReportBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ZeroCodeCucumberReportBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ZeroCodeCucumberReportBuilder uri(String uri) {
        this.uri = uri;
        return this;
    }

    public ZeroCodeCucumberReportBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ZeroCodeCucumberReportBuilder keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public ZeroCodeCucumberReportBuilder elements(CucumberElement[] cucumberElements) {
        this.cucumberElements = cucumberElements;
        return this;
    }

    public ZeroCodeCucumberReportBuilder reportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
        return this;
    }
}
